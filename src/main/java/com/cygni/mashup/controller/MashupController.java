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
    /**
     * Returns an Artist Object (mbid, description, albums (with images))
     *
     * Example request: http://localhost:8080/artist?mbid=5b11f4ce-a62d-471e-81fc-a69a8278c7da
     *
     * @param mbid
     * @return Artist
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping("/artist")
    public Artist getArtistInfo(@RequestParam String mbid) throws ExecutionException, InterruptedException {
        CompletableFuture result = (new MashupService()).getArtistMashup(mbid);
        Artist artistData = (Artist) result.get(); // blocking
        return artistData;
    }
}

