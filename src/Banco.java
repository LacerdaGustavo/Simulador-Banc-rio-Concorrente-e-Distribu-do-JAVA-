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




  
}
