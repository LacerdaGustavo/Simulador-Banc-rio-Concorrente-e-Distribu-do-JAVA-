package com.insidebank.backend;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.insidebank.database.ContaDAO;

public class Banco {
    // ConcurrentHashMap em vez de HashMap: agora contas podem ser criadas em
    // tempo real (tela de Cadastro) por threads concorrentes com seguranca,
    // sem precisar de um lock manual so para inserir no mapa.
    private final Map<Integer, Conta> contas = new ConcurrentHashMap<>(); //Armazena as contas
    private final ContaDAO dao = new ContaDAO();

    // Gerador de IDs thread-safe para novas contas (Cadastro). Comeca em 4
    // porque as contas 1, 2 e 3 ja existem fixas abaixo.
    private final AtomicInteger proximoId = new AtomicInteger(4);

    public Banco() {


        List<Conta> contasBanco = dao.listarTodas();

        if (contasBanco.isEmpty()) {

            Conta c1 = new Conta(1, 1000.0, "senha123", "Cliente Teste 1");
            Conta c2 = new Conta(2, 1000.0, "senha456", "Cliente Teste 2");
            Conta c3 = new Conta(3, 1000.0, "senha789", "Cliente Teste 3");

            dao.inserir(c1);
            dao.inserir(c2);
            dao.inserir(c3);

            contas.put(c1.getId(), c1);
            contas.put(c2.getId(), c2);
            contas.put(c3.getId(), c3);

        } else {

            for (Conta conta : contasBanco) {
                contas.put(conta.getId(), conta);
            }

        }
    }

    public Conta getConta(int id) {
        return contas.get(id);
    }


    public boolean sacar(Conta conta, double valor) {

        boolean sucesso = conta.sacar(valor);

        if (sucesso) {
            dao.atualizar(conta);
        }

        return sucesso;
    }

    public void depositar(Conta conta, double valor) {

        conta.depositar(valor);

        dao.atualizar(conta);
    }

    /**
     * Cria uma nova conta (usado pela tela de Cadastro da interface JavaFX).
     * Retorna a conta criada, ja com saldo inicial zero.
     */
    public Conta criarConta(String nome, String senha) {
        int id = proximoId.getAndIncrement();
        Conta conta = new Conta(id, 0.0, senha, nome);
        contas.put(id, conta);
        //Fazer o cadastro salvar no SQLite
        dao.inserir(conta);
        return conta;
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
                // Nota: sacar()/depositar() ja registram uma entrada generica no
                // historico ("Saque de..."/"Deposito de..."); as linhas abaixo
                // adicionam mais uma entrada especifica de transferencia, entao o
                // Extrato mostra 2 linhas por transferencia (uma por perspectiva
                // de "conta"). Simplificacao aceitavel para esta entrega.
                if (contaOrigem.getSaldo() >= valor) {
                    contaOrigem.sacar(valor);
                    contaDestino.depositar(valor);
                    
                    dao.atualizar(contaOrigem);
                    dao.atualizar(contaDestino);

                    contaOrigem.registrarTransferencia(
                            String.format("Transferencia enviada para conta %d: R$ %.2f", idDestino, valor));
                    contaDestino.registrarTransferencia(
                            String.format("Transferencia recebida da conta %d: R$ %.2f", idOrigem, valor));
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