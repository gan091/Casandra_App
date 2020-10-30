package com.android.gan091.gramaudenar.usermanagement;

public class Usuario {
    int id;
    String user,pass;

    public Usuario(){

    }

    public Usuario(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public boolean isNull(){
        if (user.equals("") || pass.equals("")){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}