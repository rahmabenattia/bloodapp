package com.example.bloodapp;

public class ReadWriteUserDetails {
    public String  age, chronic, grp;

    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails( Integer textAge, String textChronic, String textgrp){

        this.age = String.valueOf(textAge);
        this.chronic= textChronic;
        this.grp= textgrp;
    }
}
