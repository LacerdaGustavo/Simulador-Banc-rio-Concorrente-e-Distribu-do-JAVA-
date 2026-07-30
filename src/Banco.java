import java.util.HashMap;
import java.util.Map;

public class Banco {
    private final Map<Integer, Conta> contas = new HashMap<>(); //Armazena as contas

    public Banco() {
        // Populando contas iniciais para testes
        contas.put(1, new Conta(1, 1000.0, "senha123"));
        contas.put(2, new Conta(2, 1000.0, "senha456"));
        contas.put(3, new Conta(3, 1000.0, "senha789"));
    }

    public Conta getConta(int id) {
        return contas.get(id);
    }

    public boolean transferir(int idOrigem, int idDestino, double valor) {
        Conta contaOrigem = contas.get(idOrigem);
        Conta contaDestino = contas.get(idDestino);

        if (contaOrigem == null || contaDestino == null || idOrigem == idDestino || valor <= 0) {
            return false;
        }

        // PREVENÇÃO DE DEADLOCK: Ordenação de Recursos por ID
        //Sempre bloqueando primeiro a conta de menor ID e depois a de ID maior, evita a espera circular
        Conta primeiraConta = (contaOrigem.getId() < contaDestino.getId()) ? contaOrigem : contaDestino;
        Conta segundaConta = (contaOrigem.getId() < contaDestino.getId()) ? contaDestino : contaOrigem;

        primeiraConta.getLock().lock();
        try {
            segundaConta.getLock().lock();
            try {
                // Seção Crítica Atômica e Segura
                if (contaOrigem.getSaldo() >= valor) {
                    contaOrigem.sacar(valor);
                    contaDestino.depositar(valor);
                    return true;
                }
                return false;
                
            } finally {
                segundaConta.getLock().unlock();
            }
        } finally {
            primeiraConta.getLock().unlock();
        }
    }
}