package com.example.kasicaprasica.models;

public class BankCurrencyConnect {
    private int id;
    private int id_bank;
    private int id_curr;
    private int id_type;
    private int id_money_pic;
    private int amount;
    private String date;

    public BankCurrencyConnect() {
    }

    public BankCurrencyConnect(int id, int id_bank, int id_curr, int id_type, int id_money_pic, int amount, String date) {
        this.id = id;
        this.id_bank = id_bank;
        this.id_curr = id_curr;
        this.id_type = id_type;
        this.id_money_pic = id_money_pic;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_bank() {
        return id_bank;
    }

    public void setId_bank(int id_bank) {
        this.id_bank = id_bank;
    }

    public int getId_curr() {
        return id_curr;
    }

    public void setId_curr(int id_curr) {
        this.id_curr = id_curr;
    }

    public int getId_type() {
        return id_type;
    }

    public void setId_type(int id_type) {
        this.id_type = id_type;
    }

    public int getId_money_pic() {
        return id_money_pic;
    }

    public void setId_money_pic(int id_money_pic) {
        this.id_money_pic = id_money_pic;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
