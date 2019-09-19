package com.cygni.mashup.service;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class MusicBrainzService {
    private static final String url1 = "http://musicbrainz.org/ws/2/artist/";
    private static final String url2 = "?inc=url-rels+release-groups&fmt=json";

    @Async
    public CompletableFuture getMusicBrainzInformation(String mbid){

        String apiUrl = url1 + mbid + url2;

        RestTemplate restTemplate = new RestTemplate();

        MusicbrainzData result =  restTemplate.getForObject(apiUrl, MusicbrainzData.class);
        return CompletableFuture.completedFuture(result);
    }

}
