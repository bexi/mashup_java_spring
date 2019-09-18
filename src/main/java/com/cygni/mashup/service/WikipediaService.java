package com.cygni.mashup.service;

import com.cygni.mashup.repository.musicbrainzdata.MusicbrainzData;
import com.cygni.mashup.repository.musicbrainzdata.Relation;
import com.cygni.mashup.repository.wikidata.Entity;
import com.cygni.mashup.repository.wikidata.WikiData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WikipediaService {
    private static final String WIKIPEDIA_TYPE = "wikipedia";
    private static final String WIKIDATA_TYPE = "wikidata";

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    @Async
    public CompletableFuture getWikipediaDescription(MusicbrainzData musicbrainzData){

        String ID = getWikipediaID(musicbrainzData.getRelations());
        String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=" + ID;

        String TEST = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=Nirvana (band)";
        RestTemplate restTemplate = new RestTemplate();
        //WikipediaData wikipediaData =  restTemplate.getForObject(TEST, WikipediaData.class);

        ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
            // TODO throw exception
        }
        String description = getDescriptionFromJson(root);

        return CompletableFuture.completedFuture(description);
    }

    private static List<String> getKeys(JsonNode data) {
        List<String> keys = new ArrayList<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = data.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> field = it.next();
            String key = field.getKey();
            //JsonNode value = field.getValue();
            keys.add(key);
        }
        return keys;
    }

    private String getDescriptionFromJson(JsonNode json){
        //JsonNode root = mapper.readTree(json.getBody());
        JsonNode name = json.path("query").path("pages");
        List<String> keys = getKeys(name);
        logger.info(keys.toString());
        logger.info(name.get(keys.get(0)).get("extract").toString());
        //String color = jsonNode.get("color").asText();
        return (name.get(keys.get(0)).get("extract").toString());
    }

    /**
     * Get the wikipediaID from the relations data
     * @param relations
     * @return wikipediaID
     * @throws NoSuchElementException
     */
    private String getWikipediaID(List<Relation> relations) throws NoSuchElementException{
        Pair<String, String> relationsData = getRelationsData(relations);
        String type = relationsData.getKey(); // type is either wikipedia or wikidata
        String ID = relationsData.getValue(); // the id for either wikipedia or wikidata

        if(type.equals(WIKIPEDIA_TYPE)){
            return ID;
        }else if(type.equals(WIKIDATA_TYPE)){
            // retrive wikipediaID from wikiData
            try {
                return getWikipediaIdFromWikiData(ID);
            } catch (UnsupportedEncodingException e) {
                throw new NoSuchElementException();
            }
        }else throw new NoSuchElementException();
    }

    /**
     * Get the type and ID from the "wikipedia" or "wikidata" relation
     * @param relations
     * @return Pair<TypeOfRelation, ID>
     */
    private Pair<String, String> getRelationsData(List<Relation> relations) throws NoSuchElementException {
        // find a relation with a type of wikidata or wikipedia
        Optional<Relation> wikipediaOrWikidata = relations.stream()
                .filter(relation -> (
                        relation.getType().equals(WIKIPEDIA_TYPE) || relation.getType().equals(WIKIDATA_TYPE)))
                .findFirst(); // should only be one element from the filter function

        if(wikipediaOrWikidata.isPresent()){
            // get the ID to either wikipedia or wikidata from the url
            Relation wikipediaOrWikiDataRelation = wikipediaOrWikidata.get();
            String urlResource = wikipediaOrWikiDataRelation.getUrlResource();
            String[] parts = urlResource.split("/");
            String id = parts[parts.length-1];

            return new Pair<String, String>(wikipediaOrWikiDataRelation.getType(), id);
        }else throw new NoSuchElementException();
    }

    /**
     * Get the wikipedia ID from wikiData
     * @param ID
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getWikipediaIdFromWikiData(String ID) throws UnsupportedEncodingException {

        // TODO String string = String.format("A String %s %2d", aStringVar, anIntVar);
        final String wikiTitle = "enwiki";
        final String wikiDataAPI_1 = "https://www.wikidata.org/w/api.php?action=wbgetentities&ids=";
        final String wikiDataAPI_2 = "&format=json&props=sitelinks";
        String url = wikiDataAPI_1 + ID + wikiDataAPI_2;

        // TODO - move up restTemplate to parent
        RestTemplate restTemplate = new RestTemplate();
        // fetch wikidata - in this response we have the wikipedia ID
        WikiData result =  restTemplate.getForObject(url, WikiData.class);

        // TODO error handling - check http status code

        Map<String, Entity> entities = result.getEntities();
        String wikipediaID = entities.get(ID).getSitelinks().get(wikiTitle).getTitle();
        //String encodedWikipediaID = URLEncoder.encode(wikipediaID, "UTF-8");

        return wikipediaID;
    }
}
