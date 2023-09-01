package com.example.mycurrency;


import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import androidx.annotation.NonNull;

public class CurrencyList implements Serializable { // lista preferiti
    private final List<Coppia> array;
    public CurrencyList(){
        array = new ArrayList<>();
    }
    public void aggiungi(String partenza,String arrivo){
      Coppia p = new Coppia(partenza,arrivo);
      if(!contains(p)){
          array.add(p);
      }
    }
    public void rimuovi(int index){
        if(index >= 0 && index < array.size()){
            array.remove(index);
        }
    }
    public int getIndexFromPair(Coppia p){ //ritorno l'indice corrente
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            if(p.equals(array.get(i))){
                index = i;
            }
        }
        return index;
    }
    public boolean contains(Coppia p){
        return getIndexFromPair(p) >= 0;
    }
    @NonNull
    public String toString(){
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            ret.append(array.get(i).toString());
        }
        return ret.toString();
    }
    public int size(){
        return array.size();
    }
    public Coppia get(int i){
        return array.get(i);
    }
}
