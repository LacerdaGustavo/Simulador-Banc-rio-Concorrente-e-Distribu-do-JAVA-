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
                    if (partes.length != 3) return "ERRO;Formato: LOGIN;CONTA;SENHA";
                    Conta conta = banco.getConta(Integer.parseInt(partes[1]));
                    if (conta != null && conta.autenticar(partes[2])) {
                        this.contaLogada = conta;
                        return "SUCESSO;Logado na conta " + conta.getId();
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
                    if (contaLogada == null) return "ERRO;Nao autenticado";
                    int idDestino = Integer.parseInt(partes[1]);
                    double valorTransf = Double.parseDouble(partes[2]);
                    if (banco.transferir(contaLogada.getId(), idDestino, valorTransf)) {
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
                    // Comando novo (nao existia no protocolo original) - suporta
                    // a tela de Extrato da interface JavaFX. Ver Conta.getHistorico().
                    if (contaLogada == null)
                        return "ERRO;Nao autenticado";
                    List<String> historico = contaLogada.getHistorico();
                    if (historico.isEmpty())
                        return "SUCESSO;Nenhuma movimentacao registrada";
                    return "SUCESSO;" + String.join("|", historico);

                case "NOME":
                    // Comando novo - consulta o nome de uma conta pelo numero,
                    // usado na tela de confirmacao de Transferencia (mostrar
                    // o nome do destinatario antes de confirmar).
                    if (contaLogada == null)
                        return "ERRO;Nao autenticado";
                    if (partes.length != 2)
                        return "ERRO;Formato: NOME;CONTA";
                    Conta contaConsultada = banco.getConta(Integer.parseInt(partes[1]));
                    if (contaConsultada == null)
                        return "ERRO;Conta nao encontrada";
                    return "SUCESSO;" + contaConsultada.getNome();

                case "CADASTRAR":
                    // Comando novo - suporta a tela de Cadastro da interface JavaFX.
                    // Nao exige login (assim como LOGIN). Formato: CADASTRAR;NOME;SENHA
                    if (partes.length != 3)
                        return "ERRO;Formato: CADASTRAR;NOME;SENHA";
                    if (partes[1].trim().isEmpty() || partes[2].trim().isEmpty())
                        return "ERRO;Nome e senha nao podem ser vazios";
                    Conta novaConta = banco.criarConta(partes[1].trim(), partes[2]);
                    return "SUCESSO;Conta criada com numero " + novaConta.getId()
                            + ". Guarde este numero para fazer login.";

                default:
                    return "ERRO;Comando invalido";
            }
        } catch (Exception e) {
            return "ERRO;Falha no processamento numérico";
        }
    }
}