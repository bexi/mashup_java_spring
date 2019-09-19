package com.cygni.mashup.service;

import com.cygni.mashup.repository.ImageData;
import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@Service
public class CoverArtService {

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    private static final String coverArtUrl = "http://coverartarchive.org/release-group/";

    private final ImageService imageService;

    public CoverArtService(ImageService imageService){
        this.imageService = imageService;
    }
    /**
     * Get an updated album list, now with images for the albums
     *
     * @param musicbrainzData
     * @return
     */
    @Async
    public CompletableFuture<List<ReleaseGroup>> getAlbumImageUrls(MusicbrainzData musicbrainzData) {

        List<ReleaseGroup> albums = musicbrainzData.getReleaseGroups();

        List<CompletableFuture<ReleaseGroup>> completableFutures =
                albums.stream().map(album -> imageService.getAlbumImage(album)).collect(toList());

        CompletableFuture<List<ReleaseGroup>> f = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .thenApply(v -> completableFutures.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );

        return f;

    }
}
