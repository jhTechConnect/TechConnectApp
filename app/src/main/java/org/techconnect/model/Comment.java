package org.techconnect.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {


    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
    private String text;
    private String attachment;
    private String owner;
    private String createdDate;


    public Comment() {
    }

    protected Comment(Parcel in) {
        text = in.readString();
        attachment = in.readString();
        owner = in.readString();
        createdDate = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(attachment);
        parcel.writeString(owner);
        parcel.writeString(createdDate);
    }
}
