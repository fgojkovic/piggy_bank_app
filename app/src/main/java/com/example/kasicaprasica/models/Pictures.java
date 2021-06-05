package com.example.kasicaprasica.models;

import java.util.Comparator;

import static java.lang.Double.doubleToLongBits;

public class Pictures {
    private int id;
    private int id_curr;
    private int location;
    private int id_type;
    //možda bi moglo biti float
    private double value;

    public Pictures() {
    }

    public Pictures(int id, int id_curr, int location, int id_type, double value) {
        this.id = id;
        this.id_curr = id_curr;
        this.location = location;
        this.id_type = id_type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_curr() {
        return id_curr;
    }

    public void setId_curr(int id_curr) {
        this.id_curr = id_curr;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getId_type() {
        return id_type;
    }

    public void setId_type(int id_type) {
        this.id_type = id_type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Pictures other = (Pictures) obj;

        if (id == 0) {
            if (other.id != 0)
                return false;
        } else if (id != other.id) {
            return false;
        } else if (id_curr == 0) {
            if (other.id_curr != 0)
                return false;
        } else if (id_curr != other.id_curr) {
            return false;
        } else if (location == 0) {
            if (other.location != 0)
                return false;
        } else if (location != other.location) {
            return false;
        } else if (id_type == 0) {
            if (other.id_type != 0)
                return false;
        } else if (id_type != other.id_type) {
            return false;
        } else if (value == 0) {
            if(other.value != 0)
                return false;
        } else if(value != other.value) {
            return false;
        }else {
            return true;
        }

        return true;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + id_curr;
        result = prime * result + location;
        result = prime * result + id_type;
        long bits = Double.doubleToLongBits(value);
        result = (int) (bits ^ (bits >>> 32));;
        return result;
    }

}
