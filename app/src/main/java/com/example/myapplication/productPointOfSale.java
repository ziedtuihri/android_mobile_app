package com.example.myapplication;

public class productPointOfSale {

    private String idModelP, priceP, quantityP, unitP, nameP;

    public productPointOfSale(String idModelP, String priceP, String quantityP, String unitP, String namep) {
        this.idModelP = idModelP;
        this.priceP = priceP;
        this.quantityP = quantityP;
        this.unitP = unitP;
        this.nameP = namep;
    }

    public String getIdModelP() {
        return idModelP;
    }

    public String getPriceP() {
        return priceP;
    }

    public String getQuantityP() {
        return quantityP;
    }

    public String getUnitP() {
        return unitP;
    }

    public void setIdModelP(String idModelP) {
        this.idModelP = idModelP;
    }

    public void setPriceP(String priceP) {
        this.priceP = priceP;
    }

    public void setQuantityP(String quantityP) {
        this.quantityP = quantityP;
    }

    public void setUnitP(String unitP) {
        this.unitP = unitP;
    }

    public void setNameP(String nameP) {
        this.nameP = nameP;
    }

    public String getNameP() {
        return nameP;
    }
}
