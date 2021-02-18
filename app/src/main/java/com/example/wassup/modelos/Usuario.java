package com.example.wassup.modelos;

public class Usuario {

    private String id, nombreUsuario, imagenURL, estado;

    public Usuario() {}

    public Usuario(String id, String nombreUsuario, String imagenURL, String estado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.imagenURL = imagenURL;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
