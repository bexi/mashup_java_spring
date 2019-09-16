package com.cygni.mashup.controller;

import com.cygni.mashup.domain.Artist;
import com.cygni.mashup.service.MashupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class MashupController {

    // http://localhost:8080/artist?mbid=123
    @RequestMapping("/artist")
    public Artist getArtistInfo(@RequestParam String mbid) throws ExecutionException, InterruptedException {
        // get all information about the artist connected to the mbid sent in the param
        CompletableFuture result = (new MashupService()).getArtistMashup(mbid);
        Artist artistInfo = (Artist) result.get(); // blocking
        return artistInfo;
    }
}

