package com.github.vedenin.luxhistory.dict;

import com.github.vedenin.luxhistory.model.Street;
import com.github.vedenin.luxhistory.model.Town;
import com.github.vedenin.luxhistory.utils.ResourceProxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeoParser {
    private final ResourceProxy resourceProxy;
    private final static String STREET_FILE_NAME = "/dict/RUE";
    private final static String TOWN_FILE_NAME = "/dict/LOCALITE";

    public GeoParser(ResourceProxy resourceProxy) {
        this.resourceProxy = resourceProxy;
    }

    public List<Street> getStreets() {
        return getItems(STREET_FILE_NAME, GeoParser::getStreet);
    }

    public List<Town> getTowns() {
        return getItems(TOWN_FILE_NAME, GeoParser::getTown);
    }

    private <R> List<R> getItems(String name, Function<String, R> func) {
        File file = resourceProxy.getFileFromResource(name);
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
            return stream.map(func).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    private static Street getStreet(String line) {
        int k = 0;
        int size;
        size = 5;  String id = line.substring(k, k + size); k = k + size;
        size = 40; String name1 = line.substring(k, k + size); k = k + size;
        size = 40; String name2 = line.substring(k, k + size); k = k + size;
        size = 10; String shortName = line.substring(k, k + size); k = k + size;
        size = 7;  String geo1 = line.substring(k, k + size); k = k + size;
        size = 10; String date1 = line.substring(k, k + size); k = k + size;
        size = 1; String dontNeed = line.substring(k, k + size); k = k + size;
        size = 10; String data2 = line.substring(k, k + size); k = k + size;
        size = 8; String wtf1 = line.substring(k, k + size); k = k + size;
        size = 7; String geo2 = line.substring(k, k + size); k = k + size;
        size = 40; String name3 = line.substring(k);
        return new Street(id,  name1,  name2,  shortName,  geo1,  date1,  data2,  wtf1,  geo2,  name3);
    }

    private static Town getTown(String line) {
        int k = 0;
        int size;
        size = 5;  String id = line.substring(k, k + size); k = k + size;
        size = 40; String name1 = line.substring(k, k + size); k = k + size;
        size = 40; String name2 = line.substring(k, k + size); k = k + size;
        size = 3; String geo1 = line.substring(k, k + size); k = k + size;
        size = 10; String date1 = line.substring(k, k + size); k = k + size;
        size = 1; String dontNeed = line.substring(k, k + size); k = k + size;
        size = 10; String data2 = line.substring(k, k + size); k = k + size;
        size = 8; String wtf1 = line.substring(k);
        return new Town(id, name1, name2, geo1, date1, data2);
    }

    public static void main(final String[] args)
    {
        final ResourceProxy resourceProxy = new ResourceProxy();
        final GeoParser parser = new GeoParser(resourceProxy);
        List<Street> streets = parser.getStreets();
        System.out.println(streets.size());
        List<Town> towns = parser.getTowns();
        System.out.println(towns.size());
    }
}
