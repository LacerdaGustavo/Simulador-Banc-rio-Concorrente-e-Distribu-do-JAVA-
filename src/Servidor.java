import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) throws IOException {

        // Cria o banco e popula com contas iniciais de teste
        Banco banco = new Banco();
        banco.adicionarConta(new Conta(1001, 1000.0));
        banco.adicionarConta(new Conta(1002, 2000.0));
        banco.adicionarConta(new Conta(1003, 3000.0));

        // Abre o servidor na porta 4444, esperando conexões de clientes (ATMs)
        ServerSocket servidor = new ServerSocket(4444);
        System.out.println("Servidor iniciado");

        // Por enquanto ainda aceita só UM cliente (isso muda no subcommit 1.2)
        Socket cliente = servidor.accept();
        System.out.println("Cliente conectado");

        // Delega todo o atendimento desse cliente pra classe AtendimentoCliente
        AtendimentoCliente atendimento = new AtendimentoCliente(cliente, banco);
        atendimento.run(); // chamado direto (sem thread) só pra validar que a extração funcionou

        servidor.close();
    }
}
