package com.cygni.mashup.service;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@Service
public class CoverArtService {

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
        // get the albums without the associated images
        List<ReleaseGroup> albums = musicbrainzData.getReleaseGroups();

        // make the async calls
        List<CompletableFuture<ReleaseGroup>> completableFutures =
                albums.stream().map(album -> imageService.getAlbumImage(album)).collect(toList());

        // wait for the calls to finish
        CompletableFuture<List<ReleaseGroup>> albumFutures = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .thenApply(v -> completableFutures.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );

        return albumFutures;
    }
}
