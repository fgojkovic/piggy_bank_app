package com.example.kasicaprasica.comparators;

import com.example.kasicaprasica.models.Pictures;

import java.util.Comparator;

public class PictureValueComparator implements Comparator<Pictures> {
    @Override
    public int compare(Pictures o1, Pictures o2) {
        int something = Integer.compare(o1.getId_curr(), o2.getId_curr());
        return  something == 0 ? Double.compare(o2.getValue(), o1.getValue()) : something;
    }
}
