package com.example.myapplication;

public class pointOfSale {

    private int idPos ;
    private String namePos , location , address , email , phoneNumber , delegation , codeZip , city  ;


    public pointOfSale(int idPos ,String namePos, String location, String address, String email, String phoneNumber, String delegation, String codeZip, String city) {
        this.idPos = idPos ;
        this.namePos = namePos;
        this.location = location;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.delegation = delegation;
        this.codeZip = codeZip;
        this.city = city;
    }

    public pointOfSale(String namePos, String location, String address, String email, String phoneNumber, String delegation, String codeZip, String city) {
        this.namePos = namePos;
        this.location = location;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.delegation = delegation;
        this.codeZip = codeZip;
        this.city = city;
    }

    public int getIdPos() {
        return idPos;
    }

    public void setIdPos(int idPos) {
        this.idPos = idPos;
    }

    public String getNamePos() {
        return namePos;
    }

    public void setNamePos(String namePos) {
        this.namePos = namePos;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public String getCodeZip() {
        return codeZip;
    }

    public void setCodeZip(String codeZip) {
        this.codeZip = codeZip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "pointOfSale{" +
                "idPos=" + idPos +
                ", namePos='" + namePos + '\'' +
                ", location='" + location + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", delegation='" + delegation + '\'' +
                ", codeZip='" + codeZip + '\'' +
                ", ville='" + city + '\'' +
                '}';
    }
}
