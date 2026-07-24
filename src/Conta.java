public class Conta {

    private int numero;
    private double saldo;

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
        saldo = saldo + valor;
    }


    public boolean sacar(double valor){
        if (valor <=saldo){
            saldo = saldo - valor;
            return true;
        }
        return false;
    }
}
