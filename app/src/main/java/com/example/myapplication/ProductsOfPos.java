package com.example.myapplication;

import java.io.Serializable;

public class ProductsOfPos  implements Serializable {
    private String idPos ;
    private String namePos ;
    private float totalprice ;
    private int numProducts ;



    public ProductsOfPos(String idPos, String namePos, float totalprice, int numProducts) {
        this.idPos = idPos;
        this.namePos = namePos;
        this.totalprice = totalprice;
        this.numProducts = numProducts;
    }

    public String getIdPos() {
        return idPos;
    }

    public void setIdPos(String idPos) {
        this.idPos = idPos;
    }

    public String getNamePos() {
        return namePos;
    }

    public void setNamePos(String namePos) {
        this.namePos = namePos;
    }

    public float getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(float totalprice) {
        this.totalprice = totalprice;
    }

    public int getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(int numProducts) {
        this.numProducts = numProducts;
    }

    public void incrementPrduct()
    {
        this.numProducts = this.numProducts + 1 ;
    }

    public void addPrice( int price )
    {
        this.totalprice = this.totalprice + price ;
    }

    @Override
    public String toString() {
        return "ProductsOfPos{" +
                "idPos='" + idPos + '\'' +
                ", namePos='" + namePos + '\'' +
                ", totalprice=" + totalprice +
                ", numProducts=" + numProducts +
                '}';
    }

}
