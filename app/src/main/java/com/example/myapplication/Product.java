package com.example.myapplication;


public class Product {
    private int id;
    private String productName, mark, model, qrCode, unit, quantity, barCode, description, category;
    private int prix;

    public Product() {

    }

    public Product(String productName, String mark, String model, String qrCode, String unit, String quantity, String barCode, String description, String category, int prix) {
        this.productName = productName;
        this.mark = mark;
        this.model = model;
        this.qrCode = qrCode;
        this.unit = unit;
        this.quantity = quantity;
        this.barCode = barCode;
        this.description = description;
        this.category = category;
        this.prix = prix;
    }

    public Product( String productName, String mark, String model, String unit, String quantity, String category, int prix) {
        this.productName = productName;
        this.mark = mark;
        this.model = model;
        this.unit = unit;
        this.quantity = quantity;
        this.category = category;
        this.prix = prix;
    }

    public Product(int id, String productName, String mark, String model, String qrCode, String unit, String quantity, String barCode, String description, String category, int prix) {
        this.id = id;
        this.productName = productName;
        this.mark = mark;
        this.model = model;
        this.qrCode = qrCode;
        this.unit = unit;
        this.quantity = quantity;
        this.barCode = barCode;
        this.description = description;
        this.category = category;
        this.prix = prix;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public String getproductName() {
        return productName;
    }

    public String getMark() {
        return mark;
    }

    public String getModel() {
        return model;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getUnit() {
        return unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getBarCode() {
        return barCode;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getPrix() {
        return prix;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", mark='" + mark + '\'' +
                ", model='" + model + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity='" + quantity + '\'' +
                ", barCode='" + barCode + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", prix=" + prix +
                '}';
    }

}