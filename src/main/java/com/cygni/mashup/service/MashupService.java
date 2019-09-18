package com.cygni.mashup.service;

import com.cygni.mashup.domain.*;

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
        // TODO Combine Data from all API-calls
        // wait for information from musicbrainz - this info is needed to make the later api-calls
        Artist artist = (Artist) (new MusicBrainzService()).getMusicBrainzInformation(mbid).get();

        String description = (new WikipediaService()).getWikipediaDescription(artist); // TODO: make async
        List<String> imageUrl = (new CoverArtService()).getAlbumImageUrls(artist);

        // Make call to CoverArt - Async

        // Wait for the last API call to finish

        return CompletableFuture.completedFuture(artist);
    }
}
