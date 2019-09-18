package com.cygni.mashup.domain;

import java.util.List;

public class Sitelink {

    private final String site;
    private final String title;
    private final List<String> badges;

    public Sitelink(final String site, final String title, final List<String> badges) {
        this.site = site;
        this.title = title;
        this.badges = badges;
    }

    public Sitelink() {
        this.site = null;
        this.title = null;
        this.badges = null;
    }

    public String getSite() {
        return site;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getBadges() {
        return badges;
    }
}
