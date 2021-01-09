package com.example.myapplication;

public class Circuit {
    private String namePos ;
    private int numProducts ;
    private float totalPrice ;


    public Circuit(String namePos, int numProducts, float totalPrice) {
        this.namePos = namePos;
        this.numProducts = numProducts;
        this.totalPrice = totalPrice;
    }

    public String getNamePos() {
        return namePos;
    }

    public void setNamePos(String namePos) {
        this.namePos = namePos;
    }

    public int getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(int numProducts) {
        this.numProducts = numProducts;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Circuit{" +
                "namePos='" + namePos + '\'' +
                ", numProducts=" + numProducts +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
