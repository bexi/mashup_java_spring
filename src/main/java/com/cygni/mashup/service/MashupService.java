package com.cygni.mashup.service;

import com.cygni.mashup.domain.*;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Combine data from the other three services: MusicBrainz, Wikidata/Wikipedia and CoverArt
 */
@Service
public class MashupService {

    Logger logger = LoggerFactory.getLogger(MashupService.class);

    private static final String WIKIPEDIA_TYPE = "wikipedia";
    private static final String WIKIDATA_TYPE = "wikidata";

    @Async
    public CompletableFuture<Artist> getArtistMashup(String mbid) throws InterruptedException, ExecutionException {
        // TODO Combine Data from all API-calls
        // wait for information from musicbrainz - this info is needed to make the later api-calls
        Artist artist = (Artist) (new MusicBrainzService()).getMusicBrainzInformation(mbid).get();

        String description = getWikipediaDescription(artist); // async

        // Make call to CoverArt - Async

        // Wait for the last API call to finish

        return CompletableFuture.completedFuture(artist);
    }

    private String getWikipediaDescription(Artist artist){

        String ID = getWikipediaID(artist.getRelations());

        // fetch description from wikipedia

        return "";
    }

    private String getWikipediaID(List<Relation> relations) throws NoSuchElementException {
        Pair<String, String> relationsData = getRelationsData(relations);
        String type = relationsData.getKey();
        String ID = relationsData.getValue(); // the id for either wikipedia or wikidata

        if(type.equals(WIKIPEDIA_TYPE)){
            return ID;
        }else if(type.equals(WIKIDATA_TYPE)){
            // call wikidata to retrive wikipediaID
            return getWikipediaIdFromWikiData(ID);
        }else{
            throw new NoSuchElementException();
        }
    }

    /**
     *
     * @param relations
     * @return Pair<TypeOfRelation, ID>
     */
    private Pair<String, String> getRelationsData(List<Relation> relations) throws NoSuchElementException {
        Optional<Relation> wikipediaOrWikidata = relations.stream()
                .filter(relation -> (
                        relation.getType().equals(WIKIPEDIA_TYPE) || relation.getType().equals(WIKIDATA_TYPE)))
                .findFirst();
        //.collect(Collectors.toList());
        if(wikipediaOrWikidata.isPresent()){
            Relation wikipediaOrWikiDataRelation = wikipediaOrWikidata.get();
            String urlResource = wikipediaOrWikiDataRelation.getUrlResource();
            String[] parts = urlResource.split("/");
            String id = parts[parts.length-1];
            return new Pair<String, String>(wikipediaOrWikiDataRelation.getType(), id);
        }else{
            throw new NoSuchElementException();
        }
    }

    private String getWikipediaIdFromWikiData(String ID){
        /**
         * let wikiDataApiUrl = `https://www.wikidata.org/w/api.php?action=wbgetentities&ids=${wikiDataID}&format=json&props=sitelinks`;
         *       let wikiData = await axios.get(wikiDataApiUrl);
         *       // find the wikipedia-ID from second api-response
         *       let wikipediaID = wikiData.data.entities[wikiDataID].sitelinks.enwiki.title;
         **/
        final String wikiDataAPI_1 = "https://www.wikidata.org/w/api.php?action=wbgetentities&ids=";
        final String wikiDataAPI_2 = "&format=json&props=sitelinks";
        String url = wikiDataAPI_1 + ID + wikiDataAPI_2;

        RestTemplate restTemplate = new RestTemplate();

        WikiData result =  restTemplate.getForObject(url, WikiData.class);

        //logger.info(result.getEntities().get(0).getSitelinks().toString());

        Map<String, Entity> entities = result.getEntities();
        entities.forEach((k,v)->{
            logger.info("Key : " + k + " Value : " + v);
            logger.info(String.valueOf((k.equals(ID))));

        });

        logger.info(entities.get(ID).getSitelinks().toString());
        String wikipediaID = entities.get(ID).getSitelinks().get("enwiki").getTitle();
        logger.info(wikipediaID);

       /* Map<String, Object> properties = result.getProperties();
        List<String> keys = new ArrayList<String>();
        properties.forEach((k,v)->{
            logger.info("Key : " + k + " Value : " + v);
            keys.add(k);
        });
        logger.info("Length" + keys.size());
        logger.info(properties.get(keys.get(0)).getClass().toString());
        LinkedHashMap<Object, Object> ex = (LinkedHashMap<Object, Object>) properties.get(keys.get(0));
        ex.forEach((k,v)->{
            logger.info("Key : " + k + " Value : " + v);
        });*/

        return "";
    }
}
