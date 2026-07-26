import java.util.concurrent.locks.ReentrantLock;

public class Conta {

    private int numero;
    private double saldo;
    private final ReentrantLock lock = new ReentrantLock();

    public Conta(int numero, double saldo){
        this.numero = numero;
        this.saldo = saldo;
    }

    public int getNumero(){
        return numero;
    }
    
    public double getSaldo(){
        return saldo;
    }
    

    public void depositar(double valor) {
        lock.lock();
        try {
            saldo = saldo + valor;
        } finally {
            lock.unlock();
          }
    }


    public boolean sacar(double valor){
        lock.lock();
        try {
            if (valor <= saldo) {
                saldo = saldo - valor;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
}
}
