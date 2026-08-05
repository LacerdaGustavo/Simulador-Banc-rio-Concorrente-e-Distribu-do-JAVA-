package com.insidebank.backend;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class AtendimentoCliente implements Runnable {
    private final Socket socket;
    private final Banco banco;
    private boolean conectado;
    private Conta contaLogada;

    public AtendimentoCliente(Socket socket, Banco banco) {
        this.socket = socket;
        this.banco = banco;
        this.conectado = true;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            while (conectado) {
                String requisicao = in.readUTF();
                String resposta = processarComando(requisicao);
                out.writeUTF(resposta);
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + socket.getInetAddress());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String processarComando(String comando) {
        String[] partes = comando.split(";");
        String acao = partes[0].toUpperCase();

        try {
            switch (acao) {
                case "LOGIN":
                    // Formato: LOGIN;BANCO;CONTA;SENHA
                    if (partes.length != 4) return "ERRO;Formato: LOGIN;BANCO;CONTA;SENHA";
                    int bancoLogin = Integer.parseInt(partes[1]);
                    int contaLoginId = Integer.parseInt(partes[2]);
                    Conta conta = banco.getConta(bancoLogin, contaLoginId);
                    if (conta != null && conta.autenticar(partes[3])) {
                        this.contaLogada = conta;
                        return "SUCESSO;Logado no banco " + conta.getBancoId() + ", conta " + conta.getId();
                    }
                    return "ERRO;\nCredenciais invalidas";

                case "SACAR":
                    if (contaLogada == null) return "ERRO;Nao autenticado";
                    double valorSaque = Double.parseDouble(partes[1]);
                    if (banco.sacar(contaLogada,valorSaque)) {
                        return "SUCESSO;Novo saldo: " + contaLogada.getSaldo();
                    }
                    return "ERRO;Saldo insuficiente";

                case "DEPOSITAR":
                    if (contaLogada == null)
                        return "ERRO;Nao autenticado";

                    double valorDeposito = Double.parseDouble(partes[1]);

                    if (valorDeposito <= 0)
                        return "ERRO;Valor invalido";

                    banco.depositar(contaLogada, valorDeposito);

                    return "SUCESSO;Novo saldo: " + contaLogada.getSaldo();

                case "TRANSF":
                    // Formato: TRANSF;BANCO_DESTINO;CONTA_DESTINO;VALOR
                    if (contaLogada == null) return "ERRO;Nao autenticado";
                    if (partes.length != 4) return "ERRO;Formato: TRANSF;BANCO_DESTINO;CONTA_DESTINO;VALOR";
                    int bancoDestino = Integer.parseInt(partes[1]);
                    int idDestino = Integer.parseInt(partes[2]);
                    double valorTransf = Double.parseDouble(partes[3]);
                    if (banco.transferir(contaLogada.getBancoId(), contaLogada.getId(), bancoDestino, idDestino, valorTransf)) {
                        return "SUCESSO;Novo saldo: " + contaLogada.getSaldo();
                    }
                    return "ERRO;Falha na transferencia";

                case "SALDO":
                    if (contaLogada == null)
                        return "ERRO;Nao autenticado";

                    return "SUCESSO;Saldo: " + contaLogada.getSaldo();

                case "LOGOUT":
                    this.conectado = false;
                    this.contaLogada = null;
                    return "SUCESSO;Desconectado";

                case "EXTRATO":
                    if (contaLogada == null)
                        return "ERRO;Nao autenticado";
                    List<String> historico = contaLogada.getHistorico();
                    if (historico.isEmpty())
                        return "SUCESSO;Nenhuma movimentacao registrada";
                    return "SUCESSO;" + String.join("|", historico);

                case "NOME":
                    // Formato: NOME;BANCO;CONTA
                    if (contaLogada == null)
                        return "ERRO;Nao autenticado";
                    if (partes.length != 3)
                        return "ERRO;Formato: NOME;BANCO;CONTA";
                    int bancoConsultado = Integer.parseInt(partes[1]);
                    int contaConsultadaId = Integer.parseInt(partes[2]);
                    Conta contaConsultada = banco.getConta(bancoConsultado, contaConsultadaId);
                    if (contaConsultada == null)
                        return "ERRO;Conta nao encontrada";
                    return "SUCESSO;" + contaConsultada.getNome();

                case "CADASTRAR":
                    // Formato: CADASTRAR;BANCO;NOME;SENHA
                    if (partes.length != 4)
                        return "ERRO;Formato: CADASTRAR;BANCO;NOME;SENHA";
                    int bancoCadastro = Integer.parseInt(partes[1]);
                    if (partes[2].trim().isEmpty() || partes[3].trim().isEmpty())
                        return "ERRO;Nome e senha nao podem ser vazios";
                    Conta novaConta = banco.criarConta(bancoCadastro, partes[2].trim(), partes[3]);
                    if (novaConta == null)
                        return "ERRO;Banco informado nao existe";
                    return "SUCESSO;Conta criada no banco " + novaConta.getBancoId() + " com numero " + novaConta.getId()
                            + ". Guarde estes dados para fazer login.";

                default:
                    return "ERRO;Comando invalido";
            }
        } catch (Exception e) {
            return "ERRO;Falha no processamento numérico";
        }
    }
}