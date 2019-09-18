package com.cygni.mashup.service;

import com.cygni.mashup.domain.Artist;
import com.cygni.mashup.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Combine data from the other three services: MusicBrainz, Wikidata/Wikipedia and CoverArt
 */
@Service
public class MashupService {

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    @Async
    public CompletableFuture<Artist> getArtistMashup(String mbid) throws InterruptedException, ExecutionException {

        Artist artist = new Artist(mbid);

        // TODO Combine Data from all API-calls
        // wait for information from musicbrainz - this info is needed to make the later api-calls
        MusicbrainzData musicbrainzData = (MusicbrainzData) (new MusicBrainzService()).getMusicBrainzInformation(mbid).get();

        artist.setAlbums(musicbrainzData.getReleaseGroups());
        musicbrainzData.setMbid(mbid);

        String description = (new WikipediaService()).getWikipediaDescription(musicbrainzData); // TODO: make async
        List<String> imageUrl = (new CoverArtService()).getAlbumImageUrls(musicbrainzData); // TODO: implement and make async

        // Wait for the last API call to finish

        // Add new data to the artist object
        artist.setDescription(description);
        // map images to albums

        return CompletableFuture.completedFuture(artist);
    }
}
