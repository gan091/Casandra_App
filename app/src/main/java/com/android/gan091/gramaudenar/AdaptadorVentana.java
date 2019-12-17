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

public class AdaptadorVentana extends RecyclerView.Adapter<AdaptadorVentana.viewHolderVentana> {

    private List<Fuente> listaObjeto;

    AdaptadorVentana(List<Fuente> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }

    @NonNull
    @Override
    public viewHolderVentana onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_ventana,parent,false);
        return new viewHolderVentana(vista,listaObjeto);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderVentana holder, int position) {
        holder.tvAncho.setText(Float.toString(listaObjeto.get(position).getAncho()));
        holder.tvAlto.setText(Float.toString(listaObjeto.get(position).getAlto()));
        holder.tvNumPiso.setText(Integer.toString(listaObjeto.get(position).getNumPiso()));
    }

    @Override
    public int getItemCount() {
        return listaObjeto.size();
    }


    class viewHolderVentana extends RecyclerView.ViewHolder {

        TextView tvAncho,tvAlto,tvNumPiso;
        List<Fuente> listaObjeto;

        viewHolderVentana(View itemView, List<Fuente> datos) {
            super(itemView);

            tvAncho = itemView.findViewById(R.id.tvAnchoV);
            tvAlto = itemView.findViewById(R.id.tvAltoV);
            tvNumPiso = itemView.findViewById(R.id.tvNumPiso);
            listaObjeto = datos;
        }
    }
}
