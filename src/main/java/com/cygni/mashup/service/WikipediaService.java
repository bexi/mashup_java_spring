package com.cygni.mashup.service;

import com.cygni.mashup.domain.Artist;
import com.cygni.mashup.domain.Entity;
import com.cygni.mashup.domain.Relation;
import com.cygni.mashup.domain.WikiData;
import javafx.util.Pair;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class WikipediaService {
    private static final String WIKIPEDIA_TYPE = "wikipedia";
    private static final String WIKIDATA_TYPE = "wikidata";

    public String getWikipediaDescription(Artist artist){

        String ID = getWikipediaID(artist.getRelations());

        // fetch description from wikipedia

        return "";
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
        String encodedWikipediaID = URLEncoder.encode(wikipediaID, "UTF-8");

        return encodedWikipediaID;
    }
}
