package com.cygni.mashup.service;

import com.cygni.mashup.domain.Artist;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Combine data from the other three services: MusicBrainz, Wikidata/Wikipedia and CoverArt
 */
@Service
public class MashupService {

    @Async
    public CompletableFuture<Artist> getArtistMashup(String mbid) throws InterruptedException, ExecutionException {
        // TODO Combine Data from all API-calls
        // wait for information from musicbrainz - this info is needed to make the later api-calls
        Artist artist = (Artist) (new MusicBrainzService()).getMusicBrainzInformation(mbid).get();

        // Make call to wikidata / wikipedia - Async

        // Make call to CoverArt - Async

        // Wait for the last API call to finish

        return CompletableFuture.completedFuture(artist);
    }
}
