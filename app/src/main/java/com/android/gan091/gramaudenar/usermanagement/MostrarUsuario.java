package com.android.gan091.gramaudenar.usermanagement;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gan091.gramaudenar.BaseDeDatos;
import com.android.gan091.gramaudenar.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MostrarUsuario extends AppCompatActivity implements IAuxUsuario{

    RecyclerView idRecyclerView;
    ArrayList<Usuario> usuarioArrayList;
    AlertDialog alertDialog = null;
    private AdaptadorUsuario adaptadorUsuario;
    GestionUsuario gesUs;
    BaseDeDatos bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_usuario);

        FloatingActionButton fabExitUser = findViewById(R.id.fabExitUser);

        idRecyclerView = findViewById(R.id.idRecyclerView);
        usuarioArrayList = new ArrayList<Usuario>();

        gesUs = new GestionUsuario(this.getApplicationContext());
        adaptadorUsuario = new AdaptadorUsuario(this,usuarioArrayList);

        RecyclerView recyclerView = findViewById(R.id.idRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setAdapter(adaptadorUsuario);
        mostrarDatos();
        bd = new BaseDeDatos(this);

        fabExitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void mostrarDatos(){
        for(Usuario user:gesUs.selectUsuarios()){
            adaptadorUsuario.agregarUsuario(user);
        }

    }

    @Override
    public void opcionEditar(final Usuario usuario) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogLayout = layoutInflater.inflate(R.layout.form_registrar,null);

        final TextView tvTittle = dialogLayout.findViewById(R.id.tvTitleUser);
        final EditText etUser = dialogLayout.findViewById(R.id.etRegisterUser);
        final EditText etPass = dialogLayout.findViewById(R.id.etRegisterPass);

        Button btnActualizar = dialogLayout.findViewById(R.id.btnRegisterOk);
        Button btnCancelar = dialogLayout.findViewById(R.id.btnRegisterCancel);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MostrarUsuario.this);
        builder.setView(dialogLayout);
        alertDialog = builder.create();

        tvTittle.setText("ACTUALIZAR USUARIO");
        etUser.setText(usuario.getUser());
        etPass.setText(usuario.getPass());
        btnActualizar.setText("ACTUALIZAR");

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etUser.getText().toString())){
                    etUser.requestFocus();
                    etUser.setError("El usuario no puede quedar vacio");
                    return;
                }else if (TextUtils.isEmpty(etPass.getText().toString())){
                    etPass.requestFocus();
                    etPass.setError("La contraseña no puede quedar vacia");
                    return;
                }else {
                    try {
                        bd.updateUsuario(usuario.getId(),etUser.getText().toString(),etPass.getText().toString());
                        usuario.setUser(etUser.getText().toString());
                        usuario.setPass(etPass.getText().toString());
                    }
                    catch (Exception e){
                        Toast toast;
                        toast=Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                adaptadorUsuario.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void opcionEliminar(final List<Usuario> list, final int pos) {

        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);

        builder1.setTitle("Eliminación de Usuario")
                .setMessage("Esta seguro de eliminar al usuario "+list.get(pos).getUser()+"?")
                .setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bd.eliminarUsuario(list.get(pos).getId());
                                list.remove(pos);
                                adaptadorUsuario.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        builder1.create().show();


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}