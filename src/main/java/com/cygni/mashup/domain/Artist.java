package com.cygni.mashup.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//@JsonIgnoreProperties(ignoreUnknown=true)
// The @JsonIgnoreProperties annotation tells Spring to ignore any attributes not listed in the class.
public class Artist {

    @JsonProperty("id")
    private String mbid;

    @JsonProperty("country")
    private String country;

    @JsonProperty("release-groups")
    private List<ReleaseGroup> releaseGroups;

    @JsonProperty("relations")
    private List<Relation> relations;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<ReleaseGroup> getReleaseGroups() {
        return releaseGroups;
    }

    public void setReleaseGroups(List<ReleaseGroup> releaseGroups) {
        this.releaseGroups = releaseGroups;
    }
}
