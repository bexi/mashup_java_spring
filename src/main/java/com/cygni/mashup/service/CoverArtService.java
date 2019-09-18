package com.cygni.mashup.service;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;

import java.util.ArrayList;
import java.util.List;

public class CoverArtService {

    private static final String coverArtUrl = "http://coverartarchive.org/release-group/";

    /**
     * Get an updated album list, now with images for the albums
     * @param musicbrainzData
     * @return
     */
    public List<ReleaseGroup> getAlbumImageUrls(MusicbrainzData musicbrainzData){

        List<ReleaseGroup> albums = musicbrainzData.getReleaseGroups();
        List<String> ids;
        List<ReleaseGroup> albumsWithImage;

        
        // fetch album covers URL + albumID

        // return a map with albumID and imageUrl

        // map the new imageUrl to the album list


        return new ArrayList<>();
    }
}
