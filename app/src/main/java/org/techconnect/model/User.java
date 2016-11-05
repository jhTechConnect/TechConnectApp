package org.techconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Phani on 10/26/2016.
 */

public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String _id;
    private String email;
    private String name;
    private String countryCode;
    private String country;
    private String organization;
    private String pic;
    private List<String> expertises;

    public User() {
    }

    protected User(Parcel in) {
        _id = in.readString();
        email = in.readString();
        name = in.readString();
        countryCode = in.readString();
        country = in.readString();
        organization = in.readString();
        pic = in.readString();
        expertises = in.createStringArrayList();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<String> expertises) {
        this.expertises = expertises;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(countryCode);
        parcel.writeString(country);
        parcel.writeString(organization);
        parcel.writeString(pic);
        parcel.writeStringList(expertises);
    }
}
