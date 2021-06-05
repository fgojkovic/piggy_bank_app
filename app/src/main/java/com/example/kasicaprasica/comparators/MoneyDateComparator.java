package com.example.kasicaprasica.comparators;

import com.example.kasicaprasica.models.BankValueLog;

import java.util.Comparator;

public class MoneyDateComparator implements Comparator<BankValueLog> {
    @Override
    public int compare(BankValueLog o1, BankValueLog o2) {
        long first = Long.parseLong(o1.getDate());
        long second = Long.parseLong(o2.getDate());
        return Long.compare(first,second);
    }
}
