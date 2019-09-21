package com.cygni.mashup.service;

import com.cygni.mashup.domain.Artist;
import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

        CompletableFuture<String> descriptionFuture = wikipediaService.getWikipediaDescription(musicbrainzData);
        CompletableFuture<List<ReleaseGroup>> albumsFuture = coverArtService.getAlbumImageUrls(musicbrainzData);

        try {
            description = descriptionFuture.get();
            artist.setDescription(description);
        }  catch (InterruptedException | ExecutionException e) {
            logger.info(e.getCause().toString());
        }

        try {
            albums = albumsFuture.get();
            artist.setAlbums(albums);
        }  catch (InterruptedException | ExecutionException e) {
            logger.info(e.getCause().toString());
        }

        return artist;
    }

}
