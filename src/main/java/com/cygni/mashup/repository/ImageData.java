package com.cygni.mashup.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * data class to be mapped from a response from coverart
 */
public class ImageData {

    public String getImage() {
        if(images.length>=1){
            return images[0].image;
        } else return null; // no image available
    }

    @JsonProperty("images")
    private Image[] images;
    @JsonProperty("release")
    private String release;
}
@JsonIgnoreProperties({"types","front","back","edit","comment","approved","thumbnails","id"})
class Image {
    @JsonProperty("image")
    protected String image;
}