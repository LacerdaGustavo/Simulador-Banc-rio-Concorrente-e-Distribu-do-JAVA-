public class TesteBanco {

    public static void main(String[] args) {

        Banco banco = new Banco();

        banco.adicionarConta(new Conta(1001, 1000.0));
        banco.adicionarConta(new Conta(1002, 2000.0));
        banco.adicionarConta(new Conta(1003, 3000.0));

        Conta conta = banco.buscarConta(1002);

        if (conta != null) {
            System.out.println("Conta encontrada: " + conta.getNumero());
            System.out.println("Saldo: " + conta.getSaldo());
        } else {
            System.out.println("Conta não encontrada.");
        }
    }
}