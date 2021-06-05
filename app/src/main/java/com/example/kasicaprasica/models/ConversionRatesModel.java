package com.example.kasicaprasica.models;

public class ConversionRatesModel {

    private int id;
    private String code;
    private Double rate;
    //actually boolean(only 0 and 1)
    private int base;
    private String date;

    public ConversionRatesModel() {
    }


    public ConversionRatesModel(int id, String code, Double rate, int base, String date) {
        this.id = id;
        this.code = code;
        this.rate = rate;
        this.base = base;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
