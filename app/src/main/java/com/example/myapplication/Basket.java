package com.example.myapplication;

public class Basket {
    private String pos , dateAchat , idPanier ;

    public Basket(String pos, String dateAchat, String idPanier) {
        this.pos = pos;
        this.dateAchat = dateAchat;
        this.idPanier = idPanier;
    }


    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(String dateAchat) {
        this.dateAchat = dateAchat;
    }

    public String getIdPanier() {
        return idPanier;
    }

    public void setIdPanier(String idPanier) {
        this.idPanier = idPanier;
    }

    @Override
    public String toString() {
        return "Basket{" +
                "pos='" + pos + '\'' +
                ", dateAchat='" + dateAchat + '\'' +
                ", idPanier='" + idPanier + '\'' +
                '}';
    }
}
