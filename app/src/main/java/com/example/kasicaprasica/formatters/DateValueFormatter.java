package com.example.kasicaprasica.formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Date;

public class DateValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return new Date(Float.valueOf(value).longValue()).toString();
    }
}
