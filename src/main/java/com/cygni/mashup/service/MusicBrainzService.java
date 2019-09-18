package com.cygni.mashup.service;

import com.cygni.mashup.repository.MusicbrainzData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class MusicBrainzService {

    public CompletableFuture getMusicBrainzInformation(String mbid){
        String url1 = "http://musicbrainz.org/ws/2/artist/";
        String url2 = "?inc=url-rels+release-groups&fmt=json";
        String publicUrl = url1 + mbid + url2;

        //String localUrl = "http://localhost:8080/fulldata.json";
        RestTemplate restTemplate = new RestTemplate();

        MusicbrainzData result =  restTemplate.getForObject(publicUrl, MusicbrainzData.class);
        return CompletableFuture.completedFuture(result);
    }

}
