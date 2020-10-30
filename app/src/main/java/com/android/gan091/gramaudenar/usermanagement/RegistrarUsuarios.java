package com.android.gan091.gramaudenar.usermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gan091.gramaudenar.MapsActivity;
import com.android.gan091.gramaudenar.R;

public class RegistrarUsuarios extends AppCompatActivity implements View.OnClickListener {
    EditText user, pass;
    Button btnRegister, btnCancel;
    GestionUsuario gesUs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_registrar);

        user = (EditText) findViewById(R.id.etRegisterUser);
        pass = (EditText) findViewById(R.id.etRegisterPass);
        btnRegister = (Button) findViewById(R.id.btnRegisterOk);
        btnCancel = (Button) findViewById(R.id.btnRegisterCancel);

        btnRegister.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        gesUs = new GestionUsuario(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegisterOk:
                Usuario u = new Usuario();
                u.setUser(user.getText().toString());
                u.setPass(pass.getText().toString());

                if (!u.isNull()){
                    Toast.makeText(this,"Error: Campos vacios",Toast.LENGTH_LONG).show();
                }else if (gesUs.insertUsuario(u)){
                    Toast.makeText(this,"Registro Exitoso",Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(this,"Usuario ya registrado",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnRegisterCancel:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
