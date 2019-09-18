package com.cygni.mashup.repository.musicbrainzdata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data class to be mapped from a response from MUSICBRAINZ
 */
public class MusicbrainzData {

    @JsonProperty("mbid")
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
