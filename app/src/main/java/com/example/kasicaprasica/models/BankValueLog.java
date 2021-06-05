package com.example.kasicaprasica.models;

public class BankValueLog {
    private int id;
    private int id_bank;
    private int id_curr;
    private double total_value;
    private String date;

    public BankValueLog() {
    }

    public BankValueLog(int id_bank, int id_curr, double total_value, String date) {
        this.id_bank = id_bank;
        this.id_curr = id_curr;
        this.total_value = total_value;
        this.date = date;
    }

    public BankValueLog(int id, int id_bank, int id_curr, double total_value, String date) {
        this.id = id;
        this.id_bank = id_bank;
        this.id_curr = id_curr;
        this.total_value = total_value;
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

    public double getTotal_value() {
        return total_value;
    }

    public void setTotal_value(double total_value) {
        this.total_value = total_value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
