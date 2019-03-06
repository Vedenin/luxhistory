package com.github.vedenin.luxhistory.newspaper_parser;

import com.github.vedenin.luxhistory.model.Article;
import com.github.vedenin.luxhistory.utils.ResourceProxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class NewspaperDeserializer
{
    private final        ResourceProxy resourceProxy;
    private static final String        FILE_OUT_NAME = "/data/articles.out";

    public NewspaperDeserializer(final ResourceProxy resourceProxy)
    {
        this.resourceProxy = resourceProxy;
    }

    public List<Article> getArticles(final String resourceName)
    {
        final File file = resourceProxy.getFileFromResource(resourceName);
        try (final ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file)))
        {
            return (List) stream.readObject();
        }
        catch (final IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws Exception
    {
        final ResourceProxy resourceProxy = new ResourceProxy();

        final NewspaperDeserializer deserializer = new NewspaperDeserializer(resourceProxy);
        final List<Article>         articles     = deserializer.getArticles(FILE_OUT_NAME);
        System.out.print(articles.size());
    }

}