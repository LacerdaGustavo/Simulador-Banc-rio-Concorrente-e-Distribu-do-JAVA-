package com.insidebank.backend;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.mindrot.jbcrypt.BCrypt;

public class Conta {
    private final int id;
    private double saldo;
    private final String senha;
    private final String nome;
    private final int bancoId;

    // Historico de movimentacoes (usado pela tela de Extrato da interface JavaFX).
    // Lista sincronizada porque pode ser lida/escrita por threads concorrentes
    // (a mesma trava desta conta protege as escritas de saldo+historico juntas).
    private final List<String> historico = Collections.synchronizedList(new ArrayList<>());
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");

    // Trava de exclusão mútua específica para esta conta
    private final Lock lock = new ReentrantLock();

    public Conta(int id, double saldoInicial, String senha) {
        this(id, saldoInicial, senha, "Cliente " + id);
    }

    public Conta(int id, double saldoInicial, String senha, String nome, int bancoId) {
        this.id = id;
        this.saldo = saldoInicial;
        this.senha = senha;
        this.nome = nome;
        this.bancoId = bancoId;
    }

    public int getBancoId() {
        return bancoId;
    }

    public String getNome() {
        return nome;
    }


    public String getSenha() {
    return senha;
    }

    private void registrar(String descricao) {
        String linha = LocalDateTime.now().format(FORMATO_DATA) + " - " + descricao;
        historico.add(0, linha); // mais recente primeiro
    }

    /** Retorna uma copia imutavel do historico (mais recente primeiro). */
    public List<String> getHistorico() {
        synchronized (historico) {
            return List.copyOf(historico);
        }
    }

    public boolean autenticar(String senhaTentativa) {
    return BCrypt.checkpw(senhaTentativa, this.senha);
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
                registrar(String.format("Saque de R$ %.2f (saldo: R$ %.2f)", valor, saldo));
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
                registrar(String.format("Deposito de R$ %.2f (saldo: R$ %.2f)", valor, saldo));
            }
        } finally {
            lock.unlock(); 
        }
    }

    /** Usado pelo Banco.transferir(...) para registrar a movimentacao nesta conta. */
    public void registrarTransferencia(String descricao) {
        lock.lock();
        try {
            registrar(descricao);
        } finally {
            lock.unlock();
        }
    }
    
    // Exposto estritamente para a coordenação de deadlocks no Banco
    public Lock getLock() {
        return lock;
    }
}
