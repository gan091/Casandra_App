package com.android.gan091.gramaudenar.usermanagement;

import android.content.Context;
import android.database.Cursor;

import com.android.gan091.gramaudenar.BaseDeDatos;
import com.android.gan091.gramaudenar.usermanagement.Usuario;

import java.util.ArrayList;

public class GestionUsuario {
    Usuario u;
    ArrayList<Usuario> lista;
    BaseDeDatos bd;

    public GestionUsuario(Context c) {
        u = new Usuario();
        bd = new BaseDeDatos(c);
    }

    public boolean insertUsuario(Usuario u){
        if (buscar(u.getUser()) == 0){
            return (bd.insertarUsuario(u.getUser(),u.getPass()));
        }else {
            return false;
        }
    }

    public int buscar(String u){
        int x = 0;
        lista = selectUsuarios();

        for (Usuario us:lista){
            if (us.getUser().equals(u)){
                x++;
            }
        }
        return x;
    }

    public ArrayList<Usuario> selectUsuarios(){
        ArrayList<Usuario> lista = new ArrayList<Usuario>();
        lista.clear();
        bd.abrirBD();
        Cursor cu = bd.cargarDatos("tblusuarios");

        if (cu!=null && cu.moveToFirst()){
            do {
                Usuario u = new Usuario();
                u.setId(cu.getInt(0));
                u.setUser(cu.getString(1));
                u.setPass(cu.getString(2));
                lista.add(u);
            }while (cu.moveToNext());
        }
        bd.cerrarBD();
        return lista;
    }

    public int login(String u, String p){
        int a = 0;
        bd.abrirBD();
        Cursor cu = bd.cargarDatos("tblusuarios");

        if (cu!=null && cu.moveToFirst()){
            do {
                if (cu.getString(1).equals(u) && cu.getString(2).equals(p)){
                    a++;
                }
            }while (cu.moveToNext());
        }
        bd.cerrarBD();
        return a;
    }

    public Usuario getUsuario(String u, String p){
        lista = selectUsuarios();
        for (Usuario us:lista){
            if (us.getUser().equals(u) && us.getPass().equals(p)){
                return us;
            }
        }
        return null;
    }

    public Usuario getUsuarioById(int id){
        lista = selectUsuarios();
        for (Usuario us:lista){
            if (us.getId() == id){
                return us;
            }
        }
        return null;
    }
}