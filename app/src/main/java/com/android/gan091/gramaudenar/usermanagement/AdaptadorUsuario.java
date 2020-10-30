package com.android.gan091.gramaudenar.usermanagement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.gan091.gramaudenar.R;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorUsuario extends RecyclerView.Adapter<AdaptadorUsuario.usuarioView> {

    public List<Usuario> listaUsuarios = new ArrayList<>();

    private ArrayList<Usuario> usuarioArrayList;

    private IAuxUsuario iAuxUsuario;

    public AdaptadorUsuario(IAuxUsuario iAuxU, ArrayList<Usuario> listaUsuarios) {
        this.iAuxUsuario = iAuxU;
        this.listaUsuarios = listaUsuarios;
        this.usuarioArrayList = listaUsuarios;
    }

    @NonNull
    @Override
    public usuarioView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_mostrar,viewGroup,false);
        return new usuarioView(view);
    }

    @Override
    public void onBindViewHolder(usuarioView usuarioView, int i) {
        if (i == 0){
            usuarioView.btnEditUser.setVisibility(View.INVISIBLE);
            usuarioView.btnDeleteUser.setVisibility(View.INVISIBLE);
        }

        Usuario usuario = listaUsuarios.get(i);
        usuarioView.tvShowId.setText(String.valueOf(usuario.getId()));
        usuarioView.tvShowUser.setText(usuario.getUser());
        usuarioView.tvShowPass.setText(usuario.getPass());
        usuarioView.btnEditUser.setOnClickListener(new eventoEditar(usuario));
        usuarioView.btnDeleteUser.setOnClickListener(new eventoEliminar(listaUsuarios,i));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void agregarUsuario(Usuario usuario){
        listaUsuarios.add(usuario);
        this.notifyDataSetChanged();
    }

    class eventoEditar implements View.OnClickListener{

        private Usuario usuario;

        public eventoEditar(Usuario u) {
            this.usuario = u;
        }

        @Override
        public void onClick(View view) {
            iAuxUsuario.opcionEditar(usuario);
        }
    }

    class eventoEliminar implements View.OnClickListener{

        private List<Usuario> listaD;
        private int pos;

        public eventoEliminar(List<Usuario> listaD, int pos) {
            this.listaD = listaD;
            this.pos = pos;
        }

        @Override
        public void onClick(View view) {
            iAuxUsuario.opcionEliminar(listaD,pos);
        }
    }

    public class usuarioView extends RecyclerView.ViewHolder{

        private TextView tvShowId,tvShowUser, tvShowPass;
        private Button btnEditUser, btnDeleteUser;

        public usuarioView(@NonNull View itemView) {
            super(itemView);

            tvShowId = (TextView) itemView.findViewById(R.id.tvcodigoMostrar);
            tvShowUser = (TextView) itemView.findViewById(R.id.tvMostrarUser);
            tvShowPass = (TextView) itemView.findViewById(R.id.tvMostrarPass);

            btnEditUser = (Button) itemView.findViewById(R.id.btnEditUser);
            btnDeleteUser = (Button) itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
