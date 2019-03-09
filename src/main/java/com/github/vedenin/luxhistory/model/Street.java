package com.github.vedenin.luxhistory.model;

public class Street {
    private String id;
    private String name1;
    private String name2;
    private String shortName;
    private String geo1;
    private String date1;
    private String data2;
    private String wtf1;
    private String geo2;
    private String name3;

    public Street(String id, String name1, String name2, String shortName, String geo1, String date1, String data2, String wtf1, String geo2, String name3) {
        this.id = id;
        this.name1 = name1;
        this.name2 = name2;
        this.shortName = shortName;
        this.geo1 = geo1;
        this.date1 = date1;
        this.data2 = data2;
        this.wtf1 = wtf1;
        this.geo2 = geo2;
        this.name3 = name3;
    }

    public String getId() {
        return id;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public String getShortName() {
        return shortName;
    }

    public String getGeo1() {
        return geo1;
    }

    public String getDate1() {
        return date1;
    }

    public String getData2() {
        return data2;
    }

    public String getWtf1() {
        return wtf1;
    }

    public String getGeo2() {
        return geo2;
    }

    public String getName3() {
        return name3;
    }
}
