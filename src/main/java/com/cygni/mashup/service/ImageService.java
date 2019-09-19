package com.cygni.mashup.service;

import com.cygni.mashup.repository.ImageData;
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

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    Logger logger = LoggerFactory.getLogger(MashupService.class);

    @Async
    public CompletableFuture<ReleaseGroup> getAlbumImage(ReleaseGroup album) {

        // create URL
        String url = "http://coverartarchive.org/release-group/" + album.getId();

        try {
            logger.info("Running request on thread {}", Thread.currentThread().getName());
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

    public String getImageUrl(String body) {

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
