package com.github.vedenin.luxhistory.model;

import java.io.Serializable;
import java.util.Objects;

public class Article implements Serializable {

    private final String id;
    private final String description;
    private final String date;
    private final String url;
    private final String language;
    private final String type;

    public Article(String id, final String description, final String date, final String url, String language, String type) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.url = url;
        this.language = language;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getLanguage() {
        return language;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Article article = (Article) o;
        return Objects.equals(url, article.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "com.github.vedenin.luxhistory.model.Article{" +
                "description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }
}
