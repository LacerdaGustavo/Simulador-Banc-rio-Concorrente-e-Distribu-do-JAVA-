import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Conta {
    private final int id;
    private double saldo;
    private final String senha;
    
    // Trava de exclusão mútua específica para esta conta
    private final Lock lock = new ReentrantLock();

    public Conta(int id, double saldoInicial, String senha) {
        this.id = id;
        this.saldo = saldoInicial;
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public boolean autenticar(String senhaTentativa) {
        return this.senha.equals(senhaTentativa);
    }

    public double getSaldo() {
        lock.lock();
        try {
            return saldo;
        } finally {
            lock.unlock();
        }
    }

    public boolean sacar(double valor) {
        lock.lock(); // Início da Seção Crítica
        try {
            if (valor > 0 && saldo >= valor) {
                saldo -= valor;
                return true;
            }
            return false;
        } finally {
            lock.unlock(); // Fim da Seção Crítica garantido no finally
        }
    }

    public void depositar(double valor) {
        lock.lock(); // Início da Seção Crítica
        try {
            if (valor > 0) {
                saldo += valor;
            }
        } finally {
            lock.unlock(); 
        }
    }
    
    // Exposto estritamente para a coordenação de deadlocks no Banco
    public Lock getLock() {
        return lock;
    }
}