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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class WikipediaService {
    // a Relations object have a type and we are interested in these two
    private static final String WIKIPEDIA_TYPE = "wikipedia";
    private static final String WIKIDATA_TYPE = "wikidata";

    private static final String wikiDataAPI_1 = "https://www.wikidata.org/w/api.php?action=wbgetentities&ids=";
    private static final String wikiDataAPI_2 = "&format=json&props=sitelinks";

    private static final String wikipediaAPI = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=";

    // the title we are interested in
    private static final String wikiTitle = "enwiki";

    @Async
    public CompletableFuture getWikipediaDescription(MusicbrainzData musicbrainzData){

        String ID = getWikipediaID(musicbrainzData.getRelations());
        String apiUrl = wikipediaAPI + ID;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
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

    /**
     * Get all the keys from a JsonNode
     * @param data
     * @return
     */
    private static List<String> getKeys(JsonNode data) {
        List<String> keys = new ArrayList<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = data.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> field = it.next();
            String key = field.getKey();
            keys.add(key);
        }
        return keys;
    }

    /**
     * Get the description from the JSON data
     * @param json
     * @return
     */
    private String getDescriptionFromJson(JsonNode json){
        JsonNode name = json.path("query").path("pages");
        List<String> keys = getKeys(name);

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
            return getWikipediaIdFromWikiData(ID);

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
    private String getWikipediaIdFromWikiData(String ID) {
        String url = wikiDataAPI_1 + ID + wikiDataAPI_2;

        RestTemplate restTemplate = new RestTemplate();
        WikiData result =  restTemplate.getForObject(url, WikiData.class);

        Map<String, Entity> entities = result.getEntities();
        String wikipediaID = entities.get(ID).getSitelinks().get(wikiTitle).getTitle();

        return wikipediaID;
    }
}
