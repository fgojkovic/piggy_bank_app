package com.example.kasicaprasica.models;

import androidx.annotation.Nullable;

public class User {
    private int id;
    private String username;
    private String password;
    /*private String firstName;
    private String lastName;*/
    private int defCurrId;

    public User() {
    }

    public User(int id, String username, String password, int defCurrId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.defCurrId = defCurrId;
    }

    public User(String username, String password, int defCurrId) {
        this.username = username;
        this.password = password;
        this.defCurrId = defCurrId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }*/

    public int getDefCurr() {
        return defCurrId;
    }

    public void setDefCurr(int defCurrId) {
        this.defCurrId = defCurrId;
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

        final User other = (User) obj;

        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username)) {
            return false;
        } else if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password)) {
            return false;
        } else if (defCurrId == 0) {
            if (other.defCurrId != 0)
                return false;
        }else if (defCurrId != other.defCurrId) {
            return false;
        } else if (id == 0) {
            if (other.id != 0)
                return false;
        } else if (id != other.id) {
            return false;
        } else {
            return true;
        }

        return true;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((username == null) ? 0 : username.hashCode());
        return result;
    }
}
