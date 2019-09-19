package com.cygni.mashup.repository;

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

    @JsonProperty("images")
    private Image[] images;
    @JsonProperty("release")
    private String release;
}
@JsonIgnoreProperties({"types","front","back","edit","comment","approved","thumbnails","id"})
class Image {
    protected Object[] types;
    protected boolean front;
    protected boolean back;
    protected Integer edit;

    @JsonProperty("image")
    protected String image;

    protected String comment;
    protected boolean approved;
    protected Tumbnail thumbnails;
    protected String id;
}
@JsonIgnoreProperties({"large","small"})
class Tumbnail {
    protected String large;
    protected String small;
}