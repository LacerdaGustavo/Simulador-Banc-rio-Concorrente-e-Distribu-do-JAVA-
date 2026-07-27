import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    private static final int PORTA = 4444;
    private static final int MAX_THREADS = 10; // Pool de Threads Fixo

    public static void main(String[] args) {
        System.out.println("=== Servidor Bancário Iniciado na Porta " + PORTA + " ===");
        
        Banco banco = new Banco();
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Terminal conectado: " + socketCliente.getInetAddress());

                // Despacha a tarefa para o pool de threads
                AtendimentoCliente atendimento = new AtendimentoCliente(socketCliente, banco);
                pool.execute(atendimento);
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}