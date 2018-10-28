package org.charlesamccown.tchat.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document
public class Message {
    @Id
    private String id;
    private String body;
    private Date sentDate;

    public Message()
    {
        this.sentDate = new Date();
    }

    public Message(String id, String body, Date sentDate) {
        this.id = id;
        this.body = body;
        this.sentDate = sentDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Message other = (Message)o;
        return Objects.equals(id, other.id) &&
                Objects.equals(body, other.body) &&
                Objects.equals(sentDate, other.sentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body, sentDate);
    }

    @Override
    public String toString() {
        return "Message {" +
                "id='" + id + '\'' +
                ", body='" + body + '\'' +
                ", sentDate='" + sentDate + '\'' +
                '}';
    }
}
