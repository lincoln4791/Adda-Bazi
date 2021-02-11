package com.example.addabazi.selectFriend;

public class SelectFriendModelClass {
    private String userID;
    private String userName;
    private String photoName;

    public SelectFriendModelClass(String userID, String userName, String photoName) {
        this.userID = userID;
        this.userName = userName;
        this.photoName = photoName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
