Sample Twitter Client
====================

This sample Twitter client provides examples of the following: 

 * Jersey 2 REST client
 * Eclipse Maven integration
 * Twitter API
 * JDBC API
 
Usage
-------------------------------
Run from eclipse in App.java or from a Maven jar

Dependencies
-------------------------------
1. Relational database (I used postgreSQL)
2. Eclipse Mars
3. Maven
4. Twitter account

PostgreSQL Schema:
-------------------------------

# Create a table to store tweets in. 
CREATE TABLE tweets (person text, time text, tweet text);

NOTE: You will need to supply your database credentials in db.properties

Twitter set-up:
-------------------------------

In order to run this program you need to retrieve a ConsumerKey and ConsumerSecret
from twitter by registering an application at https://dev.twitter.com/apps.
Once retreieved, please put these values in twitterclient.properties.

NOTE: Be aware, there are two sets of property files. Eclipse would not recognize the standard Maven location (src/main/resources)
So I made a copy of the two property file in the root project directory.

JDBC 
-------------------------------

Insure you update the POM.xml with your database dependencies

