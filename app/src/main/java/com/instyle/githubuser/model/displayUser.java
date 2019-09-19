package com.instyle.githubuser.model;

public class displayUser {
    String userName;
    String profileImage;
    String following_url;
    String followers_url;
    String html_url;



    public displayUser(String userName, String profileImage,String html_url) {
        this.userName = userName;
        this.profileImage = profileImage;
        this.html_url = html_url;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getFollowing_url() {
        return following_url;
    }

    public String getFollowers_url() {
        return followers_url;
    }

    public String getHtml_url() {
        return html_url;
    }
}
