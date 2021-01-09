package com.example.myapplication;
public class ListProduct {
    private int id;
    private String name;
    private String list;

    public ListProduct() {

    }

    public ListProduct(int id, String name, String list) {
        this.id=id;
        this.name=name;
        this.list=list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }




}
