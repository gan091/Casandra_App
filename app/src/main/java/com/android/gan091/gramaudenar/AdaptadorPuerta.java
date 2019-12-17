package com.android.gan091.gramaudenar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class AdaptadorPuerta extends RecyclerView.Adapter<AdaptadorPuerta.viewHolderPuerta> {

    private List<Fuente> listaObjeto;

    AdaptadorPuerta(List<Fuente> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }

    @NonNull
    @Override
    public viewHolderPuerta onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_puerta,parent,false);
        return new viewHolderPuerta(vista,listaObjeto);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderPuerta holder, int position) {
        holder.tvAncho.setText(Float.toString(listaObjeto.get(position).getAncho()));
        holder.tvAlto.setText(Float.toString(listaObjeto.get(position).getAlto()));
    }

    @Override
    public int getItemCount() {
        return listaObjeto.size();
    }

    class viewHolderPuerta extends RecyclerView.ViewHolder {

        TextView tvAncho,tvAlto;
        List<Fuente> listaObjeto;

        viewHolderPuerta(View itemView, List<Fuente> datos) {
            super(itemView);

            tvAncho = itemView.findViewById(R.id.tvAnchoP);
            tvAlto = itemView.findViewById(R.id.tvAltoP);
            listaObjeto = datos;
        }
    }
}
