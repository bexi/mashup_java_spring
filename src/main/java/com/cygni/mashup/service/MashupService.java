package com.cygni.mashup.service;

import com.cygni.mashup.domain.Artist;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Combine data from the other three services: MusicBrainz, Wikidata/Wikipedia and CoverArt
 */
@Service
public class MashupService {

    // service dependencies
    private final CoverArtService coverArtService;
    private final MusicBrainzService musicBrainzService;
    private final WikipediaService wikipediaService;

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    public MashupService(MusicBrainzService musicBrainzService, WikipediaService wikipediaService, CoverArtService coverArtService) {
        this.musicBrainzService = musicBrainzService;
        this.coverArtService = coverArtService;
        this.wikipediaService = wikipediaService;
    }

    public Artist getArtistMashup(String mbid) {

        // the artist object which are to be sent with the response
        Artist artist = new Artist(mbid);

        MusicbrainzData musicbrainzData = null;
        try {
            musicbrainzData = (MusicbrainzData) musicBrainzService.getMusicBrainzInformation(mbid).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        artist.setAlbums(musicbrainzData.getReleaseGroups());
        musicbrainzData.setMbid(mbid);

        // TODO make these two requests at the same time, wait for the last one
        String description = null;
        try {
            description = (String) wikipediaService.getWikipediaDescription(musicbrainzData).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        List<ReleaseGroup> albums = null;
        try {
            albums = coverArtService.getAlbumImageUrls(musicbrainzData).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // add new data to the artist object
        artist.setDescription(description);
        artist.setAlbums(albums);

        return artist;
    }

}
