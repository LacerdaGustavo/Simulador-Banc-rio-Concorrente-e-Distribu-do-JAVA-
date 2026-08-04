package com.insidebank.net;

/**
 * Representa a resposta do servidor ja parseada.
 * O protocolo (definido em AtendimentoCliente.java) sempre responde
 * "SUCESSO;mensagem" ou "ERRO;mensagem".
 */
public class Resultado {
    private final boolean sucesso;
    private final String mensagem;

    public Resultado(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public static Resultado fromResposta(String resposta) {
        if (resposta == null || resposta.isEmpty()) {
            return new Resultado(false, "Sem resposta do servidor");
        }
        String[] partes = resposta.split(";", 2);
        boolean ok = partes[0].equalsIgnoreCase("SUCESSO");
        String msg = partes.length > 1 ? partes[1] : "";
        return new Resultado(ok, msg);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }
}
