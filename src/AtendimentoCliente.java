import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
                    return "ERRO;Credenciais invalidas";

                case "SACAR":
                    if (contaLogada == null) return "ERRO;Nao autenticado";
                    double valorSaque = Double.parseDouble(partes[1]);
                    if (contaLogada.sacar(valorSaque)) {
                        return "SUCESSO;Novo saldo: " + contaLogada.getSaldo();
                    }
                    return "ERRO;Saldo insuficiente";

                case "TRANSF":
                    if (contaLogada == null) return "ERRO;Nao autenticado";
                    int idDestino = Integer.parseInt(partes[1]);
                    double valorTransf = Double.parseDouble(partes[2]);
                    if (banco.transferir(contaLogada.getId(), idDestino, valorTransf)) {
                        return "SUCESSO;Novo saldo: " + contaLogada.getSaldo();
                    }
                    return "ERRO;Falha na transferencia";

                case "LOGOUT":
                    this.conectado = false;
                    this.contaLogada = null;
                    return "SUCESSO;Desconectado";

                default:
                    return "ERRO;Comando invalido";
            }
        } catch (Exception e) {
            return "ERRO;Falha no processamento numérico";
        }
    }
}