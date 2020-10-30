package com.android.gan091.gramaudenar.usermanagement;

import java.util.List;

public interface IAuxUsuario {

    void opcionEditar(Usuario usuario);

    void opcionEliminar(List<Usuario> list, int pos);
}
