package com.android.gan091.gramaudenar;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class Fuente {
    float ancho,alto,area;
    int numPiso;
    long idRegistro;

    public Fuente(float ancho, long idRegistro){
        this.ancho = ancho;
        this.idRegistro = idRegistro;
    }

    public Fuente(float ancho, float alto, long idRegistro) {
        this.ancho = ancho;
        this.alto = alto;
        this.idRegistro = idRegistro;
    }

    public Fuente(float ancho, float alto, int numPiso, long idRegistro) {
        this.ancho = ancho;
        this.alto = alto;
        this.numPiso = numPiso;
        this.idRegistro = idRegistro;
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

    public float getAlto() {
        return alto;
    }

    public void setAlto(float alto) {
        this.alto = alto;
    }

    public int getNumPiso() {
        return numPiso;
    }

    public void setNumPiso(int numPiso) {
        this.numPiso = numPiso;
    }

    public long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(long idRegistro) {
        this.idRegistro = idRegistro;
    }
}
