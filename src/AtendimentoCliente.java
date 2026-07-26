import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AtendimentoCliente implements Runnable {

    private Socket cliente;
    private Banco banco;

    public AtendimentoCliente(Socket cliente, Banco banco) {
        this.cliente = cliente;
        this.banco = banco;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(cliente.getInputStream());
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());

            int opcao = 0;

            while (opcao != 4) {
                opcao = in.readInt();

                if (opcao == 1) {
                    int numeroConta = in.readInt();
                    Conta conta = banco.buscarConta(numeroConta);

                    if (conta != null) {
                        out.writeUTF("Saldo: " + conta.getSaldo());
                    } else {
                        out.writeUTF("Conta não encontrada");
                    }

                } else if (opcao == 2) {
                    int numeroConta = in.readInt();
                    double valor = in.readDouble();
                    Conta conta = banco.buscarConta(numeroConta);

                    if (conta != null) {
                        conta.depositar(valor);
                        out.writeUTF("Depósito realizado. \n Saldo: R$ " + conta.getSaldo());
                    } else {
                        out.writeUTF("Conta não encontrada");
                    }

                } else if (opcao == 3) {
                    int numeroConta = in.readInt();
                    double valor = in.readDouble();
                    Conta conta = banco.buscarConta(numeroConta);

                    if (conta != null) {
                        boolean resultado = conta.sacar(valor);

                        if (resultado) {
                            out.writeUTF("Saque realizado. \n Saldo: R$ " + conta.getSaldo());
                        } else {
                            out.writeUTF("Saldo insuficiente");
                        }
                    } else {
                        out.writeUTF("Conta não encontrada");
                    }
                }
            }

            in.close();
            out.close();
            cliente.close();

        } catch (IOException e) {
            System.out.println("Erro no atendimento do cliente: " + e.getMessage());
        }
    }
}
