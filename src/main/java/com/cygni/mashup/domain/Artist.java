package com.cygni.mashup.domain;

import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;

import java.util.List;

public class Artist {
    private final String mbid;
    private String description;
    private List<ReleaseGroup> albums;

    public Artist(String mbid){
        this.mbid = mbid;
    }

    public String getMbid() {
        return mbid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ReleaseGroup> getAlbums() {
        return albums;
    }

    public void setAlbums(List<ReleaseGroup> albums) {
        this.albums = albums;
    }

}

