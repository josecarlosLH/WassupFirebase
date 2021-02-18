package com.example.wassup.modelos;

public class Chat {

    private String emisor, receptor, mensaje;
    private boolean esLeido;

    public Chat(String emisor, String receptor, String mensaje, boolean esLeido) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.esLeido = esLeido;
    }

    public Chat() {}

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isEsLeido() {
        return esLeido;
    }

    public void setEsLeido(boolean esLeido) {
        this.esLeido = esLeido;
    }
}
