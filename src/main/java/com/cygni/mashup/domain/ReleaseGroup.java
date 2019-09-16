package com.cygni.mashup.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseGroup {
    @JsonProperty("title")
    private String title;
    @JsonProperty("id")
    private String id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
