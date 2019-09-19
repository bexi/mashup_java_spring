package com.cygni.mashup.repository.wikidata;

import java.util.Map;

/**
 * data class to be mapped from a response from wikidata
 */
public class WikiData {
    private final Map<String, Entity> entities;
    private final Integer success;

    public WikiData(final Map<String, Entity> entities, final Integer success) {
        this.entities = entities;
        this.success = success;
    }

    public WikiData() {
        this.entities = null;
        this.success = null;
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public Integer getSuccess() {
        return success;
    }
}