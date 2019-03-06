package com.github.vedenin.luxhistory.model;

import java.io.Serializable;
import java.util.Objects;

public class Article implements Serializable
{
    private static final long serialVersionUID = 0L;

    private final String description;
    private final String data;
    private final String url;

    public Article(final String description, final String data, final String url)
    {
        this.description = description;
        this.data = data;
        this.url = url;
    }

    public String getDescription()
    {
        return description;
    }

    public String getData()
    {
        return data;
    }

    public String getUrl()
    {
        return url;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final Article article = (Article) o;
        return Objects.equals(url, article.url);
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(url);
    }

    @Override
    public String toString()
    {
        return "com.github.vedenin.luxhistory.model.Article{" +
                "description='" + description + '\'' +
                ", data='" + data + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
