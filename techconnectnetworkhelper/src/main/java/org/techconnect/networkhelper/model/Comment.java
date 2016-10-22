package org.techconnect.networkhelper.model;


public class Comment {


    private String text;
    private String attachment;
    private String owner;
    private String createdDate;

    /**
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return The attachment
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     * @param attachment The attachment
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
