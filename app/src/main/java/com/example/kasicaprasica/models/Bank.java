package com.example.kasicaprasica.models;

public class Bank {
    private int id;
    private String bankName;
    private String dateOfCreation;
    private String imagePath;
    private String value;


    public Bank() {

    }

    public Bank(String name, String date, String imagePath) {
        bankName = name;
        dateOfCreation = date;
        this.imagePath = imagePath;
    }

    public Bank(int id, String name, String date, String imagePath) {
        this.id = id;
        bankName = name;
        dateOfCreation = date;
        this.imagePath = imagePath;
    }

    public Bank(int id, String name, String date, String imagePath, String value) {
        this.id = id;
        bankName = name;
        dateOfCreation = date;
        this.imagePath = imagePath;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
