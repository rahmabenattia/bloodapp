package com.example.bloodapp;

public class ReadWriteUserDetails {
    public String  age, chronic;

    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails( Integer textAge, String textChronic){

        this.age = String.valueOf(textAge);
        this.chronic= textChronic;

    }
}
