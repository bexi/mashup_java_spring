package com.cygni.mashup.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Relation {
    @JsonProperty("type")
    private String type; // we are interested in the type "wikidata" or "wikipedia"
    @JsonProperty("url")
    private RelationUrl url; // contains the id to wikidata or wikipedia
}

class RelationUrl {
    @JsonProperty("id")
    private String id;
    @JsonProperty("resource")
    private String resource; // the last part of this url is the ID for wikipedia
}