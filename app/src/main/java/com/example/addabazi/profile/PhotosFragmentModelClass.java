package com.example.addabazi.profile;

public class PhotosFragmentModelClass {
    private String folderName;
    private String fileName;
    private String photoUri;
    private String timeStamp;
    private String uriPath;

    public PhotosFragmentModelClass(String folderName, String fileName, String photoUri, String timeStamp, String uriPath) {
        this.folderName = folderName;
        this.fileName = fileName;
        this.photoUri = photoUri;
        this.timeStamp = timeStamp;
        this.uriPath = uriPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }
}
