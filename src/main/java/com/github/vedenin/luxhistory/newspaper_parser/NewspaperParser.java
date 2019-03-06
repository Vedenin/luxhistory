package com.github.vedenin.luxhistory.newspaper_parser;

import com.github.vedenin.luxhistory.model.Article;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NewspaperParser
{
    private static final String TEMPLATE = "/OAI-PMH/ListRecords/record/metadata/*[name()='oai_dc:dc']/";
    private static final String DIR      = "C:\\Work\\opensource\\export01-newspapers1841-1878\\000\\";

    private final XPathExpression descriptionExpr;
    private final XPathExpression dataExpr;
    private final XPathExpression urlExpr;
    private final DocumentBuilder builder;

    public NewspaperParser() throws Exception
    {
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath        xpath        = xPathfactory.newXPath();

        descriptionExpr = xpath.compile(
                TEMPLATE + "*[name()='dc:description']");
        dataExpr = xpath.compile(
                TEMPLATE + "*[name()='dc:date']");
        urlExpr = xpath.compile(
                TEMPLATE + "*[name()='dcterms:hasVersion']");

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    public List<Article> parseDir(final String dir) throws Exception
    {

        final Collection<File> files = getFiles(dir);

        System.out.println("files " + files.size());

        return getArticles(files);
    }

    private static Collection<File> getFiles(final String dir)
    {
        System.out.println("getFiles");
        return FileUtils.listFiles(
                new File(dir),
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY
        );
    }

    private List<Article> getArticles(final Collection<File> files) throws Exception
    {
        final List<Article> articleList = new ArrayList<>();

        int i = 0;
        for (final File file : files)
        {
            final Article article = getArticle(file);
            articleList.add(article);
            i++;
            if (i % 100 == 0)
            {
                System.out.println(i + " / " + files.size());
            }
        }
        return articleList;
    }

    private Article getArticle(
            final File file) throws Exception
    {
        final Document doc = builder.parse(file);

        final String description = descriptionExpr.evaluate(doc);
        final String data        = dataExpr.evaluate(doc);
        final String url         = urlExpr.evaluate(doc);

        return new Article(description, data, url);
    }

    public static void main(final String[] args) throws Exception
    {
        final NewspaperParser parser = new NewspaperParser();

        final List<Article> articles = parser.parseDir(DIR);
        System.out.print(articles.size());
    }
}
