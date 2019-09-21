package com.cygni.mashup.service;

import com.cygni.mashup.repository.ImageData;
import com.cygni.mashup.repository.musicbrainzdata.ReleaseGroup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {

    private static final String coverArtAPI = "http://coverartarchive.org/release-group/";

    @Async
    public CompletableFuture<ReleaseGroup> getAlbumImage(ReleaseGroup album) {

        String url = coverArtAPI + album.getId();

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            // get image url string and add it to the album object
            String imageUrl = getImageUrl(response.getBody());
            album.setImageUrl(imageUrl);

            return CompletableFuture.completedFuture(album);

        } catch (HttpStatusCodeException e) {
            // if an image was not able to be fetched then set it to null
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Get the imageUrl string from the response body (json)
     * @param body
     * @return
     */
    public String getImageUrl(String body) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(body);
            ImageData imageData = mapper.convertValue(root, ImageData.class);
            return imageData.getImage();
        } catch (IOException e) {
            return null;
        }
    }
}
