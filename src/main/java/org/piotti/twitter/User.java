package org.piotti.twitter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entity into which the Twitter user is deserialized.
 *
 * @author Daniel Piotti
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @XmlElement(name = "name")
    private String name;

    public String getName() {
        return name;
    }
}