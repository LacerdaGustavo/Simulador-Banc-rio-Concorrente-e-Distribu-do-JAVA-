import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor {
        public static void main(String[] args) throws IOException{

            Banco banco = new Banco();

            // Cria algumas contas no banco
            banco.adicionarConta(new Conta(1001, 1000.0));
            banco.adicionarConta(new Conta(1002, 2000.0));
            banco.adicionarConta(new Conta(1003, 3000.0));

            ServerSocket servidor = new ServerSocket(4444);
            System.out.println("Servidor iniciado");

            Socket cliente = servidor.accept();
            System.out.println("Cliente conectado");

            DataInputStream in = new DataInputStream(cliente.getInputStream());
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());

            int opcao = 0;

            while (opcao != 4){
                opcao = in.readInt();

                if(opcao == 1){
                    int numeroConta = in.readInt();
                    Conta conta = banco.buscarConta(numeroConta);

                    if(conta != null){
                        out.writeUTF("Saldo: " + conta.getSaldo());

                    }
                    else{
                        out.writeUTF("Conta não encontrada");
                    }

                } 
                else if (opcao == 2){
                    int numeroConta = in.readInt();
                    double valor = in.readDouble();
                    Conta conta = banco.buscarConta(numeroConta);

                    if(conta != null) {
                        conta.depositar(valor);
                        out.writeUTF("Depósito realizado. \n Saldo: R$ " + conta.getSaldo());
                    }
                    else{
                        out.writeUTF("Conta não encontrada");
                    }

                }


                else if (opcao == 3){
                    int numeroConta = in.readInt();
                    double valor = in.readDouble();
                    Conta conta = banco.buscarConta(numeroConta);
                    
                    if(conta != null) {
                        boolean resultado = conta.sacar(valor);

                        if(resultado){
                            out.writeUTF("Saque realizado. \n Saldo: R$ " + conta.getSaldo());
                        }

                        else{
                            out.writeUTF("Saldo insuficiente");
                        }
                    }
                    else{
                        out.writeUTF("Conta não encontrada");
                    }


                }
            }

            
        
            in.close();
            out.close();
            cliente.close();
            servidor.close();

        }
}