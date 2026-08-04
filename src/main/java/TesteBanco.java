import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TesteBanco {
    public static void main(String[] args) {
        Banco banco = new Banco();
        double totalAntes = banco.getConta(1).getSaldo() + banco.getConta(2).getSaldo() + banco.getConta(3).getSaldo();
        System.out.println("Total no banco ANTES do Teste: R$ " + totalAntes);

        ExecutorService pool = Executors.newFixedThreadPool(50);
        Random random = new Random();

        // Dispara 5000 transferências simultâneas
        for (int i = 0; i < 5000; i++) {
            pool.execute(() -> {
                int idOrigem = random.nextInt(3) + 1;
                int idDestino = random.nextInt(3) + 1;
                if (idOrigem != idDestino) {
                    banco.transferir(idOrigem, idDestino, random.nextInt(100) + 1);
                }
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double totalDepois = banco.getConta(1).getSaldo() + banco.getConta(2).getSaldo() + banco.getConta(3).getSaldo();
        System.out.println("Total no banco DEPOIS do Teste: R$ " + totalDepois);
        
        System.out.println("\n[Saldos Finais]");
        System.out.println("Conta 1: R$ " + banco.getConta(1).getSaldo());
        System.out.println("Conta 2: R$ " + banco.getConta(2).getSaldo());
        System.out.println("Conta 3: R$ " + banco.getConta(3).getSaldo());

        if (totalAntes == totalDepois) {
            System.out.println("\n=> SUCESSO: Consistência mantida. Nenhuma Race Condition ou Deadlock!");
        } else {
            System.out.println("\n=> FALHA: Inconsistência de dados detectada.");
        }
    }
}