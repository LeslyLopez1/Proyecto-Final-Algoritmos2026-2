package com.schooltracker.proyectofinal.Model;

import java.util.Objects;

public class Arista {
    private final Vertice origen;
    private final Vertice destino;
    private double peso;

    public Arista(Vertice origen, Vertice destino, double peso) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("El origen y el destino no pueden ser nulos.");
        }
        if (origen.equals(destino)) {
            throw new IllegalArgumentException("No se permiten conexiones de un vértice consigo mismo.");
        }
        if (peso < 0 || Double.isNaN(peso) || Double.isInfinite(peso)) {
            throw new IllegalArgumentException("El peso debe ser un número no negativo.");
        }
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public Vertice getOrigen() {
        return origen;
    }

    public Vertice getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        if (peso < 0 || Double.isNaN(peso) || Double.isInfinite(peso)) {
            throw new IllegalArgumentException("El peso debe ser un número no negativo.");
        }
        this.peso = peso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arista other)) return false;
        return Double.compare(other.peso, peso) == 0
                && Objects.equals(origen, other.origen)
                && Objects.equals(destino, other.destino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, destino, peso);
    }

    @Override
    public String toString() {
        return origen.getId() + " -> " + destino.getId() + " [" + peso + "]";
    }
}