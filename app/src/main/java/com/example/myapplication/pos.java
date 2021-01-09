package com.example.myapplication;


public class pos {

    boolean isSelected;
    String nameP, adressP, idP;

    //now create constructor and getter setter method using shortcut like command+n for mac & Alt+Insert for window.


    public pos(boolean isSelected, String name, String adresP, String idP) {
        this.isSelected = isSelected;
        this.nameP = name;
        this.adressP = adresP;
        this.idP = idP;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getNameP() {
        return nameP;
    }

    public String getAdressP() {
        return adressP;
    }

    public String getIdP() {
        return idP;
    }

    public void setNameP(String nameP) {
        this.nameP = nameP;
    }

    public void setAdressP(String adressP) {
        this.adressP = adressP;
    }

    public void setIdP(String idP) {
        this.idP = idP;
    }

    @Override
    public String toString() {
        return "pos{" +
                "isSelected=" + isSelected +
                ", nameP='" + nameP + '\'' +
                ", adressP='" + adressP + '\'' +
                ", idP='" + idP + '\'' +
                '}';
    }
}