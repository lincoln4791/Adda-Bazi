package com.example.addabazi.friendRequests;

public class FriendRequestsModelClass {
    private String UserName;
    private String photoName;
    private String userID;

    public FriendRequestsModelClass(String userName, String photoName, String userID) {
        UserName = userName;
        this.photoName = photoName;
        this.userID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
