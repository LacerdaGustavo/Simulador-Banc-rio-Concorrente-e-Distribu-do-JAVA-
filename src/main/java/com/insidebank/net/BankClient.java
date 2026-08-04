package com.insidebank.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Cliente de socket para a interface JavaFX.
 * Fala exatamente o mesmo protocolo texto que o AtendimentoCliente.java
 * do servidor ja implementa (LOGIN;CONTA;SENHA / SACAR;VALOR / etc).
 *
 * Diferente do Cliente.java original (que roda em um loop bloqueante de
 * console), esta classe expoe um metodo por comando para ser chamado a
 * partir dos Controllers, sempre dentro de uma javafx.concurrent.Task
 * para nao travar a UI Thread.
 */
public class BankClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public void conectar(String host, int porta) throws IOException {
        socket = new Socket(host, porta);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    private synchronized Resultado enviar(String comando) throws IOException {
        out.writeUTF(comando);
        String resposta = in.readUTF();
        return Resultado.fromResposta(resposta);
    }

    public Resultado login(int contaId, String senha) throws IOException {
        return enviar("LOGIN;" + contaId + ";" + senha);
    }

    public Resultado sacar(double valor) throws IOException {
        return enviar("SACAR;" + valor);
    }

    public Resultado depositar(double valor) throws IOException {
        return enviar("DEPOSITAR;" + valor);
    }

    public Resultado transferir(int contaDestino, double valor) throws IOException {
        return enviar("TRANSF;" + contaDestino + ";" + valor);
    }

    public Resultado saldo() throws IOException {
        return enviar("SALDO");
    }

    public Resultado logout() throws IOException {
        return enviar("LOGOUT");
    }

    /** Comando novo (ver AtendimentoCliente.java) - consulta o nome de uma conta pelo numero. */
    public Resultado consultarNome(int contaId) throws IOException {
        return enviar("NOME;" + contaId);
    }

    /** Comando novo (ver AtendimentoCliente.java) - lista o historico da conta logada. */
    public Resultado extrato() throws IOException {
        return enviar("EXTRATO");
    }

    /** Comando novo (ver AtendimentoCliente.java) - cria uma conta nova. Nao precisa estar logado. */
    public Resultado cadastrar(String nome, String senha) throws IOException {
        return enviar("CADASTRAR;" + nome + ";" + senha);
    }

    public boolean isConectado() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void fechar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
