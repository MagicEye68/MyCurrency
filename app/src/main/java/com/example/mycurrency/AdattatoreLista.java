package com.example.mycurrency;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdattatoreLista extends RecyclerView.Adapter<AdattatoreLista.MioViewHolder>  { //crea le celle della lista
    private final CurrencyList dati;
    public AdattatoreLista(CurrencyList dati){
        this.dati = dati;
    }
    class MioViewHolder extends RecyclerView.ViewHolder {
        private final TextView partenza;
        private final TextView arrivo;
        public MioViewHolder(@NonNull View itemView) {
            super(itemView);
            partenza = itemView.findViewById(R.id.partenza);
            arrivo = itemView.findViewById(R.id.arrivo);
            ImageView freccia = itemView.findViewById(R.id.freccia);

            Context context = itemView.getContext();
            freccia.setOnClickListener(view -> {// click sulla freccia

                int indice = getAdapterPosition();
                String string1 = dati.get(indice).first;
                String string2 = dati.get(indice).second;

                //creazione intent
                Intent intent = new Intent("passovalori");
                Bundle bundle = new Bundle();
                bundle.putString("partenza",string1);
                bundle.putString("arrivo",string2);
                intent.putExtras(bundle);

                context.sendBroadcast(intent);
                ((Activity)context).finish();
            });
        }
        public void costruisci(String partenza, String arrivo){
            this.partenza.setText(partenza);
            this.arrivo.setText(arrivo);
        }
    }

    @NonNull
    @Override
    public AdattatoreLista.MioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento, parent, false);
        return new MioViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull AdattatoreLista.MioViewHolder holder, int position) {
        Coppia p = dati.get(position);
        holder.costruisci(p.first,p.second);
    }
    @Override
    public int getItemCount() {
        return dati.size();
    }
}
