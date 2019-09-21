package com.cygni.mashup.service;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MusicBrainzService {
    private static final String url1 = "http://musicbrainz.org/ws/2/artist/";
    private static final String url2 = "?inc=url-rels+release-groups&fmt=json";

    public MusicbrainzData getMusicBrainzInformation(String mbid){

        String apiUrl = url1 + mbid + url2;

        RestTemplate restTemplate = new RestTemplate();

        try {
            MusicbrainzData result = restTemplate.getForObject(apiUrl, MusicbrainzData.class);
            return result;
        }catch (HttpClientErrorException e){
            throw new ResponseStatusException(
                    e.getStatusCode(), "Client Error in MusicBrainz API", e);
        }catch (HttpServerErrorException e) {
            throw new ResponseStatusException(
                    e.getStatusCode(), "Server Error in MusicBrainz API", e);
        }
    }
}
