package com.github.vedenin.luxhistory.newspaper_parser;

import com.github.vedenin.luxhistory.model.Article;
import com.github.vedenin.luxhistory.utils.ResourceProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class NewspaperSpliter {
    private static final String        FILE_OUT_NAME = "/data/articles.out";
    private static final String PATH = "/home/slava/programming_new/luxhistory/src/main/resources/";

    private static final int START_YEAR = 1841;
    private static final int LAST_YEAR = 1878;
    private final NewspaperDeserializer newspaperDeserializer;

    public NewspaperSpliter(NewspaperDeserializer newspaperDeserializer) {
        this.newspaperDeserializer = newspaperDeserializer;
    }

    public void splitAndSave(final String resourceName, final String outDir) {
        final List<Article> articles = newspaperDeserializer.getArticles(resourceName);
        for(int year = START_YEAR; year <= LAST_YEAR; year++) {
            final String yearInString =  Integer.toString(year);
            final List<Article> articlesByYear = articles.
                    stream().
                    filter(a -> a.getData().contains(yearInString)).
                    collect(Collectors.toList());
            final String newName = resourceName.replaceAll(".out", yearInString + ".out");
            System.out.println(articlesByYear.size());
            save(outDir + newName, articlesByYear);
        }
    }

    public void save(final String fileName, List<Article> articles)
    {
        final File file     = new File(fileName);
        try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
        {
            oos.writeObject(articles);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args)
    {
        final ResourceProxy resourceProxy = new ResourceProxy();

        final NewspaperDeserializer deserializer = new NewspaperDeserializer(resourceProxy);
        final NewspaperSpliter spliter = new NewspaperSpliter(deserializer);
        spliter.splitAndSave(FILE_OUT_NAME, PATH);
    }
}
