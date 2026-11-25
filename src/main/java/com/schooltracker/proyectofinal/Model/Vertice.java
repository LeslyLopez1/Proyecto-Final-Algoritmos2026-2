package com.schooltracker.proyectofinal.Model;

public class Vertice {
    private final int id;
    private String etiqueta;

    public Vertice(int id) {
        this(id, String.valueOf(id));
    }

    public Vertice(int id, String etiqueta) {
        this.id = id;
        this.etiqueta = (etiqueta == null || etiqueta.isBlank()) ? String.valueOf(id) : etiqueta.trim();
    }

    public int getId() {
        return id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        if(etiqueta != null && !etiqueta.isBlank()) {
            this.etiqueta = etiqueta.trim();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertice other)) return false;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return etiqueta.equals(String.valueOf(id)) ? String.valueOf(id) : id + " (" + etiqueta + ")";
    }
}
