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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CoverArtService {

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    private static final String coverArtUrl = "http://coverartarchive.org/release-group/";

    /**
     * Get an updated album list, now with images for the albums
     * @param musicbrainzData
     * @return
     */
    public List<ReleaseGroup> getAlbumImageUrls(MusicbrainzData musicbrainzData){

        List<ReleaseGroup> albums = musicbrainzData.getReleaseGroups();

        // Get all images async
        List<CompletableFuture<ReleaseGroup>> futures = albums.stream()
                .map(album -> getAlbumImage(album))
                .collect(Collectors.toList());

        // Create a combined Future using allOf()
        CompletableFuture<Void> allFutures_void = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[futures.size()])
        );

        CompletableFuture<List<ReleaseGroup>> allFeatures = allFutures_void.thenApply(v -> {
            return futures.stream()
                    .map(future -> future.join())
                    .collect(Collectors.toList());
        });

        // TODO error handling
        try {
            List<ReleaseGroup> albumsWithImages = allFeatures.get();
            return albums;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Async
    CompletableFuture<ReleaseGroup> getAlbumImage(ReleaseGroup album)  {

        // create URL
        String url = "http://coverartarchive.org/release-group/" + album.getId();

        try{
            // make http call
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            // get image and add it to the album
            String image = getImageUrl(response.getBody());
            album.setImageUrl(image);
            return CompletableFuture.completedFuture(album);
        } catch (HttpStatusCodeException e) {
            // LOGGER.debug(" Unable to get cover art for album '{}', got HTTP status code: {}", releaseGroup.getTitle(), e.getStatusCode());
            return CompletableFuture.completedFuture(null); // ApiResponse.error(e)
            // TODO: handle error
        }
    }

    private String getImageUrl(String body){

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(body);

            ImageData imageData = mapper.convertValue(root, ImageData.class);

            return imageData.getImage();

        } catch (IOException e) {
            e.printStackTrace();
            // TODO throw exception
            return "null";
        }
    }
}
