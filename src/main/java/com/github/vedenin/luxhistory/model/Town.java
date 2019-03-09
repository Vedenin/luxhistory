package com.github.vedenin.luxhistory.model;

public class Town {
   private String id;
   private String name1;
   private String name2;
   private String geo1;
   private String date1;
   private String data2;

    public String getId() {
        return id;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
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

    public Town(String id, String name1, String name2, String geo1, String date1, String data2) {
        this.id = id;
        this.name1 = name1;
        this.name2 = name2;
        this.geo1 = geo1;
        this.date1 = date1;
        this.data2 = data2;
    }
}
