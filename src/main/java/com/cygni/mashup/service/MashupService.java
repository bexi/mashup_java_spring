package com.cygni.mashup.service;

import com.cygni.mashup.domain.Artist;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Combine data from the other three services: MusicBrainz, Wikidata/Wikipedia and CoverArt
 */
@Service
public class MashupService {

    private final CoverArtService coverArtService;
    private final MusicBrainzService musicBrainzService;
    private final WikipediaService wikipediaService;

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    public MashupService(MusicBrainzService musicBrainzService, WikipediaService wikipediaService, CoverArtService coverArtService) {
        this.musicBrainzService = musicBrainzService;
        this.coverArtService = coverArtService;
        this.wikipediaService = wikipediaService;
    }

    @Async
    public CompletableFuture<Artist> getArtistMashup(String mbid) {

        Artist artist = new Artist(mbid);

        // TODO Combine Data from all API-calls
        // wait for information from musicbrainz - this info is needed to make the later api-calls
        MusicbrainzData musicbrainzData = null; // blocking
        try {
            musicbrainzData = (MusicbrainzData) musicBrainzService.getMusicBrainzInformation(mbid).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        artist.setAlbums(musicbrainzData.getReleaseGroups());
        musicbrainzData.setMbid(mbid);

        String description = null; //
        try {
            description = (String) wikipediaService.getWikipediaDescription(musicbrainzData).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        List<ReleaseGroup> albums = null; //
        try {
            albums = coverArtService.getAlbumImageUrls(musicbrainzData).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Wait for the last API call to finish

        // Add new data to the artist object
        artist.setDescription(description);
        artist.setAlbums(albums);

        return CompletableFuture.completedFuture(artist);
    }


}
