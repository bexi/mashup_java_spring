package com.cygni.mashup.repository.musicbrainzdata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseGroup {
    @JsonProperty("title")
    private String title;
    @JsonProperty("id")
    private String id;

    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId(){
        return this.id;
    }
}
