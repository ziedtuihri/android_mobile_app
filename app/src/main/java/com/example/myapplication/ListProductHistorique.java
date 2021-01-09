package com.example.myapplication;

public class ListProductHistorique {

    private String name;
    private String date;
    private String idHistorique;
    private String idList;


    public ListProductHistorique()
    {

    }
    public ListProductHistorique(String name, String date, String idHistorique, String idList) {
        this.name = name;
        this.date = date;
        this.idHistorique = idHistorique;
        this.idList = idList;
    }

    public ListProductHistorique( String date, String idHistorique, String idList) {
        this.name = name;
        this.date = date;
        this.idHistorique = idHistorique;
        this.idList = idList;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdHistorique() {
        return idHistorique;
    }

    public void setIdHistorique(String idHistorique) {
        this.idHistorique = idHistorique;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }

    @Override
    public String toString() {
        return "ListProductHistorique{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", idHistorique='" + idHistorique + '\'' +
                ", idList='" + idList + '\'' +
                '}';
    }
}
