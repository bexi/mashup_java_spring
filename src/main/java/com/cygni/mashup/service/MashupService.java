package com.cygni.mashup.service;

import com.cygni.mashup.domain.Artist;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Combine data from the other three services: MusicBrainz, Wikidata/Wikipedia and CoverArt
 */
@Service
public class MashupService {

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    // service dependencies
    private final CoverArtService coverArtService;
    private final MusicBrainzService musicBrainzService;
    private final WikipediaService wikipediaService;

    public MashupService(MusicBrainzService musicBrainzService, WikipediaService wikipediaService, CoverArtService coverArtService) {
        this.musicBrainzService = musicBrainzService;
        this.coverArtService = coverArtService;
        this.wikipediaService = wikipediaService;
    }

    public Artist getArtistMashup(String mbid) {

        Artist artist = new Artist(mbid);
        String description = null;
        List<ReleaseGroup> albums = null;

        MusicbrainzData musicbrainzData = musicBrainzService.getMusicBrainzInformation(mbid);

        artist.setAlbums(musicbrainzData.getReleaseGroups());
        artist.setDescription(description);
        musicbrainzData.setMbid(mbid);

        // TODO make these two requests at the same time, wait for the last one
        try {
            description = (String) wikipediaService.getWikipediaDescription(musicbrainzData).get();
            artist.setDescription(description);
        } catch (InterruptedException | ExecutionException e) {
            logger.info(e.getCause().toString());
        }

        try {
            albums = coverArtService.getAlbumImageUrls(musicbrainzData).get();
            artist.setAlbums(albums);
        } catch (InterruptedException | ExecutionException e) {
            logger.info(e.getCause().toString());
        }

        return artist;
    }

}
