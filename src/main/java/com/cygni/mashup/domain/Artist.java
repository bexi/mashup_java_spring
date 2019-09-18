package com.cygni.mashup.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//@JsonIgnoreProperties(ignoreUnknown=true)
// The @JsonIgnoreProperties annotation tells Spring to ignore any attributes not listed in the class.
public class Artist {

    @JsonProperty("id")
    private String mbid;

    @JsonProperty("release-groups")
    private List<ReleaseGroup> releaseGroups;

    @JsonProperty("relations")
    private List<Relation> relations;

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public List<ReleaseGroup> getReleaseGroups() {
        return releaseGroups;
    }

    public void setReleaseGroups(List<ReleaseGroup> releaseGroups) {
        this.releaseGroups = releaseGroups;
    }
}
