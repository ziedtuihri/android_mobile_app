package com.example.myapplication;

public class ProductMicroService {
    private int idMS;
    private String idProductMS, idListMS, productNameMS, markMS, modelMS, qrCodeMS, unitMS, quantityMS, barCodeMS, descriptionMS, categoryMS, idModelMS;
    private int prixMS;

    public ProductMicroService(int idMS, String idListMS, String productNameMS, String markMS, String modelMS, String qrCodeMS, String unitMS, String quantityMS, String barCodeMS, String descriptionMS, String categoryMS, int prixMS, String idModelMS, String idProductMS) {
        this.idMS = idMS;
        this.idListMS = idListMS;
        this.productNameMS = productNameMS;
        this.markMS = markMS;
        this.modelMS = modelMS;
        this.qrCodeMS = qrCodeMS;
        this.unitMS = unitMS;
        this.quantityMS = quantityMS;
        this.barCodeMS = barCodeMS;
        this.descriptionMS = descriptionMS;
        this.categoryMS = categoryMS;
        this.prixMS = prixMS;
        this.idModelMS = idModelMS;
        this.idProductMS = idProductMS;
    }

    public ProductMicroService(String productNameMS, String markMS, String modelMS, String unitMS, String quantityMS, String categoryMS, int prixMS) {
        this.productNameMS = productNameMS;
        this.markMS = markMS;
        this.modelMS = modelMS;
        this.unitMS = unitMS;
        this.quantityMS = quantityMS;
        this.categoryMS = categoryMS;
        this.prixMS = prixMS;
    }

    public String getIdProductMS() {
        return idProductMS;
    }

    public void setIdProductMS(String idProductMS) {
        this.idProductMS = idProductMS;
    }

    public void setIdMS(int idMS) {
        this.idMS = idMS;
    }

    public void setIdModelMS(String idModelMS) {
        this.idModelMS = idModelMS;
    }

    public String getIdModelMS() {
        return idModelMS;
    }

    public void setIdListMS(String idListMS) {
        this.idListMS = idListMS;
    }

    public void setProductNameMS(String productNameMS) {
        this.productNameMS = productNameMS;
    }

    public void setMarkMS(String markMS) {
        this.markMS = markMS;
    }

    public void setModelMS(String modelMS) {
        this.modelMS = modelMS;
    }

    public void setQrCodeMS(String qrCodeMS) {
        this.qrCodeMS = qrCodeMS;
    }

    public void setUnitMS(String unitMS) {
        this.unitMS = unitMS;
    }

    public void setQuantityMS(String quantityMS) {
        this.quantityMS = quantityMS;
    }

    public void setBarCodeMS(String barCodeMS) {
        this.barCodeMS = barCodeMS;
    }

    public void setDescriptionMS(String descriptionMS) {
        this.descriptionMS = descriptionMS;
    }

    public void setCategoryMS(String categoryMS) {
        this.categoryMS = categoryMS;
    }

    public void setPrixMS(int prixMS) {
        this.prixMS = prixMS;
    }

    public int getIdMS() {
        return idMS;
    }

    public String getIdListMS() {
        return idListMS;
    }

    public String getProductNameMS() {
        return productNameMS;
    }

    public String getMarkMS() {
        return markMS;
    }

    public String getModelMS() {
        return modelMS;
    }

    public String getQrCodeMS() {
        return qrCodeMS;
    }

    public String getUnitMS() {
        return unitMS;
    }

    public String getQuantityMS() {
        return quantityMS;
    }

    public String getBarCodeMS() {
        return barCodeMS;
    }

    public String getDescriptionMS() {
        return descriptionMS;
    }

    public String getCategoryMS() {
        return categoryMS;
    }

    public int getPrixMS() {
        return prixMS;
    }
}
