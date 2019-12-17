package com.android.gan091.gramaudenar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class AdaptadorFachada extends RecyclerView.Adapter<AdaptadorFachada.viewHolderFachada> {

    List<Fuente> listaObjeto;

    public AdaptadorFachada(List<Fuente> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }

    @Override
    public viewHolderFachada onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_fachada,parent,false);
        return new viewHolderFachada(vista,listaObjeto);
    }

    @Override
    public void onBindViewHolder(viewHolderFachada holder, int position) {
        holder.tvAncho.setText(Float.toString(listaObjeto.get(position).getAncho()));
    }

    @Override
    public int getItemCount() {
        return listaObjeto.size();
    }

    public class viewHolderFachada extends RecyclerView.ViewHolder {

        TextView tvAncho;
        List<Fuente> listaObjeto;

        public viewHolderFachada(View itemView, List<Fuente> datos) {
            super(itemView);

            tvAncho = (TextView) itemView.findViewById(R.id.tvAnchoF);
            listaObjeto = datos;
        }
    }
}

