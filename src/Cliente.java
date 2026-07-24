import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;



public class Cliente {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",4444);
        
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        Scanner teclado = new Scanner(System.in);
        int opcao = 0;

        while(opcao != 4){
            System.out.println("\n---- CAIXA ELETRÔNICO ----");
            System.out.println("1 - Consultar saldo");
            System.out.println("2 - Depositar ");
            System.out.println("3 - Sacar ");
            System.out.println("4 - Sair ");

            opcao = teclado.nextInt();
            out.writeInt(opcao);


            //Consultar
            if (opcao == 1){
                System.out.println("Número da conta: ");
                int numeroConta = teclado.nextInt();
                out.writeInt(numeroConta);
                System.out.println(in.readUTF());
            }

            //Depositar
            else if (opcao == 2) {
                System.out.println("Número da conta: ");
                int numeroConta = teclado.nextInt();

                System.out.println("Valor do depósito: ");
                double valor = teclado.nextDouble();
                out.writeInt(numeroConta);
                out.writeDouble(valor);
                System.out.println(in.readUTF());
            }

            //Sacar
            else if (opcao == 3) {
                System.out.println("Número da conta: ");
                int numeroConta = teclado.nextInt();

                System.out.println("Valor do saque: ");
                double valor = teclado.nextDouble();
                out.writeInt(numeroConta);
                out.writeDouble(valor);
                System.out.println(in.readUTF());
            }


            //Sair
            else if (opcao == 4){
                System.out.println("Encerrando a conexão");
            }

            //Opção inválida
            else{
                System.out.println("Opção inválida");
            }
        }


        teclado.close();
        in.close();
        out.close();
        socket.close();  
    
    
    }
}
