import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        // Cria um pool fixo de 10 threads reutilizáveis, em vez de criar uma thread nova do zero para cada cliente. Assim, o servidor reaproveita threads
        // já existentes do pool para atender cada conexão.
        ExecutorService pool = Executors.newFixedThreadPool(10);

    while (true) {
        Socket cliente = servidor.accept();
        System.out.println("Cliente conectado: " + cliente.getInetAddress());

        AtendimentoCliente atendimento = new AtendimentoCliente(cliente, banco);
        pool.submit(atendimento); // Envia o atendimento pro Pool, o qual escolhe uma Thread para rodar. Caso as 10 threads estejam ocupadas, o próximo cliente fica na fila até uma liberar.
    }

        servidor.close();
    }
}
