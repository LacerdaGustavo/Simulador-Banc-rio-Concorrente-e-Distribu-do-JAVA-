package com.insidebank.backend;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.mindrot.jbcrypt.BCrypt;

public class InstituicaoBancaria {
    private final int id;
    private final String nome;

    // Cada instituicao guarda suas proprias contas, isoladas das demais.
    // IDs de conta sao unicos APENAS dentro desta instituicao: duas
    // instituicoes diferentes podem ter contas com o mesmo numero.
    private final Map<Integer, Conta> contas = new ConcurrentHashMap<>();

    // Gerador de IDs thread-safe, proprio desta instituicao.
    private final AtomicInteger proximoId;

    public InstituicaoBancaria(int id, String nome, int idInicial) {
        this.id = id;
        this.nome = nome;
        this.proximoId = new AtomicInteger(idInicial);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Conta getConta(int contaId) {
        return contas.get(contaId);
    }

    /** Usado ao carregar contas existentes do banco de dados (ver Banco). */
    public void adicionarConta(Conta conta) {
        contas.put(conta.getId(), conta);
        
         // Garante que o próximo ID será sempre maior que qualquer conta já existente.
        proximoId.updateAndGet(idAtual -> Math.max(idAtual, conta.getId() + 1));

    }

    /**
     * Cria uma nova conta nesta instituicao.
     * Nota: so cria em memoria - a persistencia (dao.inserir) continua
     * sendo responsabilidade do Banco, que e quem tem o ContaDAO.
     */
    public Conta criarConta(String nome, String senha) {
        int novoId = proximoId.getAndIncrement();
        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt());
        Conta conta = new Conta(novoId, 0.0, senhaHash, nome, this.id);
        contas.put(novoId, conta);
        return conta;
    }
}