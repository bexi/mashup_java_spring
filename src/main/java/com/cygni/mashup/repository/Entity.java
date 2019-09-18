package com.cygni.mashup.repository;

import java.util.Map;

public class Entity {

    private final String type;
    private final String id;
    private final Map<String, Sitelink> sitelinks;

    public Entity(final String type, final String id, final Map<String, Sitelink> sitelinks) {
        this.type = type;
        this.id = id;
        this.sitelinks = sitelinks;
    }

    public Entity() {
        this.type = null;
        this.id = null;
        this.sitelinks = null;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Map<String, Sitelink> getSitelinks() {
        return sitelinks;
    }
}


