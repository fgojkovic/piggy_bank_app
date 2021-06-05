package com.example.kasicaprasica.models;

public class MyCurrency {
    private int id;
    private String name;
    private String code;
    private String symbol;

    public MyCurrency() {}

    public MyCurrency(int id, String name, String code, String symbol) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
