package com.github.vedenin.luxhistory.newspaper_parser;

import com.github.vedenin.luxhistory.model.Article;
import com.github.vedenin.luxhistory.utils.ResourceProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class NewspaperSerializer
{
    private static final String DIR           = "/home/slava/programming_new/export01-newspapers1841-1878/";
    private static final String FILE_OUT_NAME = "/data/articles.out";

    private final NewspaperParser newspaperParser;
    private final ResourceProxy   resourceProxy;

    public NewspaperSerializer(final NewspaperParser newspaperParser,
                               final ResourceProxy resourceProxy)
    {
        this.newspaperParser = newspaperParser;
        this.resourceProxy = resourceProxy;
    }

    public void save(final String resourceName) throws Exception
    {
        final List<Article> articles = newspaperParser.parseDir(DIR);
        final File          file     = resourceProxy.getFileFromResource(resourceName);

        try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
        {
            oos.writeObject(articles);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws Exception
    {
        final NewspaperParser parser        = new NewspaperParser();
        final ResourceProxy   resourceProxy = new ResourceProxy();

        final NewspaperSerializer serializer = new NewspaperSerializer(parser, resourceProxy);
        serializer.save(FILE_OUT_NAME);

    }
}
