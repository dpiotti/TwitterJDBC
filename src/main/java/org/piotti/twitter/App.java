package org.piotti.twitter;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Uses Jersey OAuth client library to authenticate with Twitter.
 *
 * @author Daniel Piotti
 */
public class App {

	public static void main(final String[] args) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
		Properties props = new Properties();

		// Get the consumer key/secret and token/secret from the property file.
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("twitterclient.properties");
			props.load(fis);
		} catch (final IOException e) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException ex) {
				}
			}
		}

		// Make sure the keys are in the twitterclient.properties file.
		if (props.getProperty("consumerKey") == null || props.getProperty("consumerSecret") == null) {
			System.out.println("Missing consumerKey and/or consumerSecret found in twitterclient.properties file");
			System.exit(1);
		}

		// Set ConsumerCredenetials
		final ConsumerCredentials consumerCredentials = new ConsumerCredentials(props.getProperty("consumerKey"),
				props.getProperty("consumerSecret"));

		final Feature filterFeature;

		// Authenticate if not already done.
		if (props.getProperty("token") == null) {

			// Perfom the Authorization
			final OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
					.authorizationFlow("https://api.twitter.com/oauth/request_token",
							"https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize")
					.build();
			final String authorizationUri = authFlow.start();

			System.out.println("Enter the URI into a web browser to authorize:");
			System.out.println(authorizationUri);
			System.out.print("Enter the authorization code: ");
			String verifier;
			try {
				verifier = in.readLine();
			} catch (final IOException ex) {
				throw new RuntimeException(ex);
			}
			AccessToken accessToken = authFlow.finish(verifier);

			props.setProperty("token", accessToken.getToken());
			props.setProperty("tokenSecret", accessToken.getAccessTokenSecret());

			filterFeature = authFlow.getOAuth1Feature();
		} else {
			AccessToken storedToken = new AccessToken(props.getProperty("token"), props.getProperty("tokenSecret"));

			filterFeature = OAuth1ClientSupport.builder(consumerCredentials).feature().accessToken(storedToken).build();
		}

		final Client client = ClientBuilder.newBuilder().register(filterFeature).register(JacksonFeature.class).build();

		final Response response = client.target("https://api.twitter.com/1.1/statuses/home_timeline.json").request()
				.get();
		if (response.getStatus() != 200) {
			String errorEntity = null;
			if (response.hasEntity()) {
				errorEntity = response.readEntity(String.class);
			}
			throw new RuntimeException("Error code: " + response.getStatus() + ", reason: "
					+ response.getStatusInfo().getReasonPhrase() + ", entity: " + errorEntity);
		}

		// Update twitterclient.properties
		FileOutputStream fis1 = null;
		try {
			fis1 = new FileOutputStream("twitterclient.properties");
			props.store(fis1, null);
		} catch (final IOException e) {
		} finally {
			try {
				if (fis1 != null) {
					fis1.close();
				}
			} catch (final IOException ex) {
			}
		}

		// Connect to database.
		Connection connection = JDBC.DatabaseConnect();

		if (connection != null) {
			System.out.println("Connected to database!");
		} else {
			System.out.println("Failed to make connection!");
			System.exit(1);
		}

		// Marshall JAX-RS JSON response into a list of Status entities.
		final List<Status> statuses = response.readEntity(new GenericType<List<Status>>() {
		});

		PreparedStatement ps = null;

		// Finally add tweets to database.
		// TODO: Add logic to incrementally add tweets.
		for (final Status s : statuses) {

			try {
				String query = "INSERT INTO tweets (person, time, tweet) VALUES (?, ?, ?)";
				ps = connection.prepareStatement(query);
				ps.setString(1, s.getUser().getName());
				ps.setString(2, s.getCreatedAt());
				ps.setString(3, s.getText());
				ps.execute();

			} catch (SQLException se) {
				se.printStackTrace();
			}

		}
		System.out.println("Tweets added to database.");

	}

}