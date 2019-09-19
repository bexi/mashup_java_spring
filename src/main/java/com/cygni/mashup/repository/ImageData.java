package com.cygni.mashup.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageData {
    public Image[] getImages() {
        return images;
    }

    public String getImage() {
        return images[0].image;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }
    @JsonProperty("images")
    private Image[] images;
    @JsonProperty("release")
    private String release;
}
@JsonIgnoreProperties({"types","front","back","edit","comment","approved","thumbnails","id"})
class Image {
    @JsonIgnore
    protected Object[] types;
    @JsonIgnore
    protected boolean front;
    @JsonIgnore
    protected boolean back;
    @JsonIgnore
    protected Integer edit;

    @JsonProperty("image")
    protected String image;
    @JsonIgnore
    protected String comment;

    @JsonIgnore
    protected boolean approved;
    @JsonIgnore
    protected Tumbnail thumbnails;
    @JsonIgnore
    protected String id;
}
@JsonIgnoreProperties({"large","small"})
class Tumbnail {
    protected String large;
    protected String small;
}