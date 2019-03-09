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

public class NlpMaster {

    public static void main(String ... args) throws IOException {
        String s = detectLanguage("estava em uma marcenaria na Rua Bruno");
        NlpMaster nlpMaster = new NlpMaster();
        Map<String, Integer> map = nlpMaster.nounPosDetector("Staat in feiner Richtung für Beginn und Fortgang dieses Abenteuers verantwortlich gemacht werben darf, so wird nichts anderes übrig bleiben, als den künftigen Kaiser von Mcrico feinem Schicksale zu überlassen");
        System.out.println(map);
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

    public Map<String, Integer> nounPosDetector(String text) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        InputStream inputStreamPOSTagger = getClass()
                .getResourceAsStream("/nlp/models/de-pos-maxent.bin");
        POSModel posModel = new POSModel(inputStreamPOSTagger);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String tags[] = posTagger.tag(tokens);

        for (int i=0;i<tags.length;i++){
            if (tags[i].equals("NNP") || tags[i].equals("NN")){
                int count = map.getOrDefault(tokens[i], 0);
                map.put(tokens[i], count + 1);
            }
        }
        return map;

    }
}
