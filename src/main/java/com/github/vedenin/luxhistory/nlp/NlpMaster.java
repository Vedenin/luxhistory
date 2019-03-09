package com.github.vedenin.luxhistory.nlp;

import com.github.vedenin.luxhistory.model.Article;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class NlpMaster {

    private final Client client;

    Map<String, Integer> deNouns = new HashMap<>();

    Map<String, Integer> deAdjectives = new HashMap<>();

    Map<String, Integer> frNouns = new HashMap<>();

    Map<String, Integer> frAdjectives = new HashMap<>();

    InputStream inputStreamPOSTaggerDe = null;
    InputStream inputStreamPOSTaggerFr = null;
    POSModel posModelDe = null;
    POSModel posModelFr = null;
    POSTaggerME posTaggerDe = null;
    POSTaggerME posTaggerFr = null;

    public NlpMaster() throws UnknownHostException {
        client = new PreBuiltTransportClient(
                Settings.builder().put("client.transport.sniff", true)
                        .put("cluster.name", "elasticsearch").build())
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    }


    public static void main(String... args) throws IOException {
        NlpMaster nlpMaster = new NlpMaster();
        nlpMaster.calculatePopNouns();

        System.out.println("De" + topNKeys(nlpMaster.deNouns, 20));
        System.out.println("Fr" + topNKeys(nlpMaster.frNouns, 20));

        System.out.println("De" + topNMap(nlpMaster.deNouns, 20));
        System.out.println("Fr" + topNMap(nlpMaster.frNouns, 20));

    }

    private void processNouns(List<Map<String, Object>> allDocs) throws IOException {
        for (Map<String, Object> doc : allDocs) {
            String language = (String) doc.get("language");
            if ("de".equalsIgnoreCase(language)) {
                nounPosDetector(deNouns, (String) doc.get("description"), "de");
            } else if ("fr".equalsIgnoreCase(language)) {
                nounPosDetector(frNouns, (String) doc.get("description"), "fr");
            }
        }
    }

    public void calculatePopNouns() throws IOException {
        int startYear = 1841;
        int endYear = 1878;
        int scrollSize = 100;
        for (int j = startYear; j <= endYear; j++) {
            List<Map<String, Object>> esData = new ArrayList<Map<String, Object>>();
            SearchResponse response = null;
            int i = 0;
            while (i < 10) {
                response = client.prepareSearch("articles")
                        .setTypes("_doc")
                        .setQuery(QueryBuilders.rangeQuery("date").to(j + 1 + "-01-01").from(j + "-01-01"))
                        .setSize(scrollSize)
                        .setFrom(i * scrollSize)
                        .execute()
                        .actionGet();
                for (SearchHit hit : response.getHits()) {
                    esData.add(hit.getSource());
                }
                i++;
                System.out.println(i);
                processNouns(esData);
                esData.clear();

            }
            Map<String, Integer> mapDe = topNMap(deNouns, 20);
            Map<String, Integer> mapFr = topNMap(frNouns, 20);
            BulkRequestBuilder bulkRequest = client.prepareBulk();


            bulkRequest.add(client.prepareIndex("pop_nouns", "_doc", String.valueOf(j))
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("de_nounsCount", mapDe.toString())
                            .field("fr_nounsCount", mapFr.toString())
                            .endObject()
                    )
            );

            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                System.out.println("FAIL:" + bulkResponse.buildFailureMessage());
            }
            deNouns.clear();
            frNouns.clear();
        }
        //return esData;
    }

    public void nounPosDetector(Map<String, Integer> map, String text, String lang) throws IOException {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);


        if (lang.startsWith("de")) {
            if (inputStreamPOSTaggerDe == null) {
                inputStreamPOSTaggerDe = getClass()
                        .getResourceAsStream("/nlp/models/de-pos-maxent.bin");
                posModelDe = new POSModel(inputStreamPOSTaggerDe);
                posTaggerDe = new POSTaggerME(posModelDe);
            }
        } else if (lang.startsWith("fr")) {
            if (inputStreamPOSTaggerFr == null) {
                inputStreamPOSTaggerFr = getClass()
                        .getResourceAsStream("/nlp/models/fr-pos.bin");
                posModelFr = new POSModel(inputStreamPOSTaggerFr);
                posTaggerFr = new POSTaggerME(posModelFr);
            }
        }

        String tags[] = null;
        if (lang.startsWith("de")) {
            tags = posTaggerDe.tag(tokens);
        } else tags = posTaggerFr.tag(tokens);


        for (int i = 0; i < tags.length; i++) {
            if (tags[i].equals("NNP") || tags[i].equals("NN") || tags[i].equals("NPP")) {
                int count = map.getOrDefault(tokens[i], 0);
                if (tokens[i].length() > 3)
                    map.put(tokens[i], count + 1);
            }
        }
    }

    public void adjectivePosDetector(Map<String, Integer> map, String text, String lang) throws IOException {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        InputStream inputStreamPOSTagger = null;
        if (lang.startsWith("de")) {
            inputStreamPOSTagger = getClass()
                    .getResourceAsStream("/nlp/models/de-pos-maxent.bin");
        } else if (lang.startsWith("fr")) {
            inputStreamPOSTagger = getClass()
                    .getResourceAsStream("/nlp/models/fr-pos.bin");
        }
        POSModel posModel = new POSModel(inputStreamPOSTagger);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String tags[] = posTagger.tag(tokens);

        for (int i = 0; i < tags.length; i++) {
            if (tags[i].startsWith("ADJ")) {
                int count = map.getOrDefault(tokens[i], 0);
                map.put(tokens[i], count + 1);
            }
        }
    }
    public static List topNKeys(final Map<String, Integer> map, int n) {
        PriorityQueue<String> topN = new PriorityQueue<>(n, Comparator.comparingInt(map::get));

        for (String key : map.keySet()) {
            if (topN.size() < n)
                topN.add(key);
            else if (map.get(topN.peek()) < map.get(key)) {
                topN.poll();
                topN.add(key);
            }
        }
        return (List) Arrays.asList(topN.toArray());
    }

    public static Map<String, Integer> topNMap(final Map<String, Integer> map, int n) {

        return map.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .limit(n)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}
