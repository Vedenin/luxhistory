package com.github.vedenin.luxhistory.nlp;

import opennlp.tools.langdetect.*;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NlpMaster {

    ConcurrentHashMap<String, Integer> nouns = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, Integer> adjectives = new ConcurrentHashMap<>();

    public static void main(String ... args) throws IOException {
        String s = detectLanguage("estava em uma marcenaria na Rua Bruno");
        NlpMaster nlpMaster = new NlpMaster();
        nlpMaster.nounPosDetector("Marseille. Ce samedi, le rendez-vous est donné à 13h pour les gilets jaunes, qui marcheront ensuite dans la ville. Pour l'heure, le programme de la marche n'est pas connu.", "fr");
        System.out.println(nlpMaster.nouns);
    }

    public static String detectLanguage(String text) throws IOException {
        InputStreamFactory dataIn
                = new MarkableFileInputStreamFactory(
                new File("src/main/resources/nlp/models/DoccatSample.txt"));
        ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
        LanguageDetectorSampleStream sampleStream
                = new LanguageDetectorSampleStream(lineStream);
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 100);
        params.put(TrainingParameters.CUTOFF_PARAM, 5);
        params.put("DataIndexer", "TwoPass");
        params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");

        LanguageDetectorModel model = LanguageDetectorME
                .train(sampleStream, params, new LanguageDetectorFactory());

        LanguageDetector ld = new LanguageDetectorME(model);
        Language[] languages = ld
                .predictLanguages(text);

        Language l = Arrays.stream(languages).max(Comparator.comparing(Language::getConfidence)).orElseThrow(NoSuchElementException::new);

        return l.getLang();
    }

    public void nounPosDetector(String text, String lang) throws IOException {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        InputStream inputStreamPOSTagger = null;
        if (lang.startsWith("de")){
            inputStreamPOSTagger =  getClass()
                    .getResourceAsStream("/nlp/models/de-pos-maxent.bin");
        }
        else if (lang.startsWith("fr")){
            inputStreamPOSTagger =  getClass()
                    .getResourceAsStream("/nlp/models/fr-pos.bin");
        }
        POSModel posModel = new POSModel(inputStreamPOSTagger);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String tags[] = posTagger.tag(tokens);

        for (int i=0;i<tags.length;i++){
            if (tags[i].equals("NNP") || tags[i].equals("NN") || tags[i].equals("NPP")){
                int count = nouns.getOrDefault(tokens[i], 0);
                nouns.put(tokens[i], count + 1);
            }
        }
    }

    public void adjectivePosDetector(String text, String lang) throws IOException {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        InputStream inputStreamPOSTagger = null;
        if (lang.startsWith("de")){
            inputStreamPOSTagger =  getClass()
                    .getResourceAsStream("/nlp/models/de-pos-maxent.bin");
        }
        else if (lang.startsWith("fr")){
            inputStreamPOSTagger =  getClass()
                    .getResourceAsStream("/nlp/models/fr-pos.bin");
        }
        POSModel posModel = new POSModel(inputStreamPOSTagger);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String tags[] = posTagger.tag(tokens);

        for (int i=0;i<tags.length;i++){
            if (tags[i].startsWith("ADJ")){
                int count = adjectives.getOrDefault(tokens[i], 0);
                adjectives.put(tokens[i], count + 1);
            }
        }
    }

    public static List topNKeys(final HashMap<String, Integer> map, int n) {
        PriorityQueue<String> topN = new PriorityQueue<>(n, Comparator.comparingInt(map::get));

        for(String key:map.keySet()){
            if (topN.size() < n)
                topN.add(key);
            else if (map.get(topN.peek()) < map.get(key)) {
                topN.poll();
                topN.add(key);
            }
        }
        return (List) Arrays.asList(topN.toArray());
    }

}
