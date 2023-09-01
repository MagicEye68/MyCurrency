package com.example.mycurrency;

import java.io.Serializable;
public class Coppia implements Serializable {//classe per gestire i preferiti
    public String first;
    public String second;
    public Coppia(String first, String second){
        this.first = first;
        this.second = second;
    }
    public boolean equals(Coppia obj){
        return first.equals(obj.first) && second.equals(obj.second);
    }
}
