package com.cygni.mashup.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Relation {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RelationUrl getUrl() {
        return url;
    }

    public void setUrl(RelationUrl url) {
        this.url = url;
    }

    public String getUrlResource(){
        return this.url.resource;
    }

    @JsonProperty("type")
    private String type; // we are interested in the type "wikidata" or "wikipedia"
    @JsonProperty("url")
    private RelationUrl url; // contains the id to wikidata or wikipedia
}

class RelationUrl {
    @JsonProperty("id")
    protected String id;
    @JsonProperty("resource")
    protected String resource; // the last part of this url is the ID for wikipedia
}