package com.insidebank.backend;

public class InstituicaoBancaria {
    private final int id;
    private final String nome;

    public InstituicaoBancaria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
