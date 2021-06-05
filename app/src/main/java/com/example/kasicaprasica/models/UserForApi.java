package com.example.kasicaprasica.models;

import com.google.gson.annotations.SerializedName;

public class UserForApi {

    @SerializedName("name")
    public String name;
    @SerializedName("job")
    public String job;
    @SerializedName("id")
    public String id;
    @SerializedName("createdAt")
    public String createdAt;

    public UserForApi(String name, String job) {
        this.name = name;
        this.job = job;
    }


}
