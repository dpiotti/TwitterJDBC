package org.piotti.twitter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entity into which the Twitter status (tweet) is deserialized
 *
 *  @author Daniel Piotti
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {

    @XmlElement(name = "created_at")
    private String createdAt;
    @XmlElement(name = "text")
    private String text;
    @XmlElement(name = "user")
    private User user;
    
    @XmlElement(name = "retweet_count")
    private int retweetCount;

    public int getRetweetCount() {
		return retweetCount;
	}

	public String getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }
}