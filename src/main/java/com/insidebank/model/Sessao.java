package com.insidebank.model;

import com.insidebank.net.BankClient;

/**
 * Guarda o estado da sessao atual (conexao + conta logada) para que os
 * controllers das varias telas (Dashboard, Transferencia, Extrato, ...)
 * compartilhem a mesma conexao de socket sem precisar reabrir login.
 *
 * Singleton simples - suficiente para um app desktop de usuario unico.
 */
public class Sessao {
    private static final Sessao INSTANCE = new Sessao();

    private BankClient client;
    private int contaId;
    private String tema = "dark"; // "dark" ou "light" - ver Configuracoes > Geral > Tema
    // quando uma chamada externa quiser abrir Configuracoes já com um painel selecionado,
    // preencha esta propriedade com: "geral", "minhaConta", "privacidade" ou "ajuda"
    private String abrirConfiguracoesPainel = null;

    private Sessao() {
    }

    public static Sessao getInstance() {
        return INSTANCE;
    }

    public BankClient getClient() {
        return client;
    }

    public void setClient(BankClient client) {
        this.client = client;
    }

    public int getContaId() {
        return contaId;
    }

    public void setContaId(int contaId) {
        this.contaId = contaId;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getAbrirConfiguracoesPainel() {
        return abrirConfiguracoesPainel;
    }

    public void setAbrirConfiguracoesPainel(String abrirConfiguracoesPainel) {
        this.abrirConfiguracoesPainel = abrirConfiguracoesPainel;
    }

    public void encerrar() {
        if (client != null) {
            client.fechar();
        }
        client = null;
        contaId = 0;
    }
}
