package com.github.vedenin.luxhistory.newspaper_parser;

import com.github.vedenin.luxhistory.model.Article;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class NewspaperParser {
    private static final String TEMPLATE = "/OAI-PMH/ListRecords/record/metadata/*[name()='oai_dc:dc']/";
    private static final String DIR = "C:\\work\\365690\\export01-newspapers1841-1878\\";
    private final Client client;

    private final XPathExpression descriptionExpr;
    private final XPathExpression dateExpr;
    private final XPathExpression urlExpr;
    private final XPathExpression languageExpr;
    private final XPathExpression typeExpr;
    private final XPathExpression idExpr;
    private final DocumentBuilder builder;

    public NewspaperParser() throws Exception {
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();

        descriptionExpr = xpath.compile(
                TEMPLATE + "*[name()='dc:description']");
        dateExpr = xpath.compile(
                TEMPLATE + "*[name()='dc:date']");
        urlExpr = xpath.compile(
                TEMPLATE + "*[name()='dcterms:hasVersion']");
        languageExpr = xpath.compile(
                TEMPLATE + "*[name()='dc:language']");
        typeExpr = xpath.compile(
                TEMPLATE + "*[name()='dc:type']");
        idExpr = xpath.compile(
                TEMPLATE + "*[name()='dc:identifier']");


        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        client  = new PreBuiltTransportClient(
                Settings.builder().put("client.transport.sniff", true)
                        .put("cluster.name", "elasticsearch").build())
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    }

    public List<Article> parseDir(final String dir) throws Exception {

        final Collection<File> files = getFiles(dir);

        System.out.println("files " + files.size());

        return getArticles(files);
    }

    private static Collection<File> getFiles(final String dir) {
        System.out.println("getFiles");
        return FileUtils.listFiles(
                new File(dir),
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY
        );
    }

    private List<Article> getArticles(final Collection<File> files) throws Exception {
        final List<Article> articleList = new ArrayList<>();

        int i = 0;
        for (final File file : files) {
            final Article article = getArticle(file);
            articleList.add(article);
            i++;
            if (i % 100 == 0) {
                System.out.println(i + " / " + files.size());
            }
        }
        return articleList;
    }

    private Article getArticle(
            final File file) throws Exception {
        final Document doc = builder.parse(file);

        final String description = descriptionExpr.evaluate(doc);
        final String data = dateExpr.evaluate(doc);
        final String url = urlExpr.evaluate(doc);
        final String language = languageExpr.evaluate(doc);
        final String type = typeExpr.evaluate(doc);
        final String id = idExpr.evaluate(doc);

        return new Article(id, description, data, url, language, type);
    }

    public static void main(final String[] args) throws Exception {
        final NewspaperParser parser = new NewspaperParser();

        File dir = new File(DIR);
        String[] childs = dir.list();
        for (String child : childs) {
            parser.populate(parser, child);
        }
    }

    private void populate(NewspaperParser parser, String child) throws Exception {

        System.out.println(">>>" + child);
        final List<Article> articles = parser.parseDir(DIR + File.separator + child);



        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for (Article article : articles) {
            bulkRequest.add(client.prepareIndex("articles", "_doc", article.getId())
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("description", article.getDescription())
                            .field("date", article.getDate())
                            .field("url", article.getUrl())
                            .field("language", article.getLanguage())
                            .field("type", article.getType())
                            .endObject()
                    ).setPipeline("langdetect-pipeline")
            );
        }

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            System.out.println("FAIL:" + bulkResponse.buildFailureMessage());
        }
    }

}
