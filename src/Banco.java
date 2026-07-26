import java.util.ArrayList;


public class Banco {

    private ArrayList<Conta> contas; //Lista que armazena objetos do tipo conta

    public Banco(){
        contas = new ArrayList<Conta>(); //Construtor inicia com lista vazia
    }

    public void adicionarConta(Conta conta){
        contas.add(conta);
    }

    public Conta buscarConta(int numero){
        for (int i=0; i < contas.size(); i++){
            Conta conta = contas.get(i);
            if(conta.getNumero() == numero){
                return conta;
            }
        }
        return null;
    }

    public boolean transferir(int numeroOrigem, int numeroDestino, double valor) {
    Conta origem = buscarConta(numeroOrigem);
    Conta destino = buscarConta(numeroDestino);

    // Se alguma das contas não existe, não há o que transferir
    if (origem == null || destino == null) {
        return false;
    }

    // Regra anti-deadlock: sempre trava primeiro a conta de MENOR número,
    // independente de qual é origem ou destino. Assim, se dois clientes
    // fizerem transferências em direções opostas ao mesmo tempo (A→B e B→A),
    // ambos vão tentar travar a mesma conta primeiro — nunca formam
    // espera circular.
    Conta primeira = (origem.getNumero() < destino.getNumero()) ? origem : destino;
    Conta segunda = (origem.getNumero() < destino.getNumero()) ? destino : origem;

    primeira.travar();
    try {
        segunda.travar();
        try {
            // Tenta sacar da origem; sacar() já é protegido internamente
            // (ReentrantLock permite re-travar pela mesma thread)
            boolean sucesso = origem.sacar(valor);

            if (sucesso) {
                destino.depositar(valor);
                return true;
            } else {
                // Saldo insuficiente: transferência cancelada, nada foi debitado
                return false;
            }
        } finally {
            segunda.destravar();
        }
    } finally {
        primeira.destravar();
    }
}




  
}
