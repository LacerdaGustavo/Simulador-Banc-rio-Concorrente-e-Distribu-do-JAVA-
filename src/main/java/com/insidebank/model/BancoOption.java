package com.insidebank.model;

/**
 * Representa uma instituicao bancaria para preencher os ComboBox de
 * selecao de banco nas telas de Login, Cadastro e Transferencia.
 *
 * Nota: a lista de bancos disponiveis (ver BANCOS_DISPONIVEIS) espelha
 * as instituicoes fixas criadas em Banco.java no servidor. Nao existe
 * hoje um comando no protocolo para consultar essa lista dinamicamente -
 * fica registrado como melhoria futura (ex: comando LISTAR_BANCOS).
 */
public class BancoOption {
    private final int id;
    private final String nome;

    public BancoOption(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    /** Usado pelo ComboBox para decidir o texto exibido para cada opcao. */
    @Override
    public String toString() {
        return nome;
    }

    /** Lista fixa de bancos disponiveis - espelha Banco.java no servidor. */
    public static final BancoOption[] BANCOS_DISPONIVEIS = {
            new BancoOption(1, "Banco Alpha"),
            new BancoOption(2, "Banco Beta"),
            new BancoOption(3, "Banco Gamma")
    };
}