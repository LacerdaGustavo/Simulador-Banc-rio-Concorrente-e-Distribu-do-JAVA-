package com.insidebank.backend;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mindrot.jbcrypt.BCrypt;

import com.insidebank.database.ContaDAO;

public class Banco {

    // IDs fixos das instituicoes pre-cadastradas no sistema.
    private static final int BANCO_ALPHA_ID = 1;
    private static final int BANCO_BETA_ID = 2;
    private static final int BANCO_GAMMA_ID = 3;

    // Cada instituicao bancaria isola suas proprias contas.
    private final Map<Integer, InstituicaoBancaria> instituicoes = new ConcurrentHashMap<>();

    private final ContaDAO dao = new ContaDAO();

    public Banco() {

        InstituicaoBancaria bancoAlpha = new InstituicaoBancaria(BANCO_ALPHA_ID, "Banco Alpha", 4);
        InstituicaoBancaria bancoBeta = new InstituicaoBancaria(BANCO_BETA_ID, "Banco Beta", 1);
        InstituicaoBancaria bancoGamma = new InstituicaoBancaria(BANCO_GAMMA_ID, "Banco Gamma", 1);

        instituicoes.put(BANCO_ALPHA_ID, bancoAlpha);
        instituicoes.put(BANCO_BETA_ID, bancoBeta);
        instituicoes.put(BANCO_GAMMA_ID, bancoGamma);

        List<Conta> contasSalvas = dao.listarTodas();

        if (contasSalvas.isEmpty()) {
            // Primeira execucao: cria as 3 contas de teste no Banco Alpha,
            // igual ja acontecia antes do suporte a multiplos bancos.
            Conta c1 = new Conta(1, 1000.0, BCrypt.hashpw("senha123", BCrypt.gensalt()), "Cliente Teste 1", BANCO_ALPHA_ID);
            Conta c2 = new Conta(2, 1000.0, BCrypt.hashpw("senha456", BCrypt.gensalt()), "Cliente Teste 2", BANCO_ALPHA_ID);
            Conta c3 = new Conta(3, 1000.0, BCrypt.hashpw("senha789", BCrypt.gensalt()), "Cliente Teste 3", BANCO_ALPHA_ID);

            dao.inserir(c1);
            dao.inserir(c2);
            dao.inserir(c3);

            bancoAlpha.adicionarConta(c1);
            bancoAlpha.adicionarConta(c2);
            bancoAlpha.adicionarConta(c3);

        } else {
            // Redistribui cada conta salva para a instituicao correta,
            // de acordo com o bancoId gravado no banco de dados.
            for (Conta conta : contasSalvas) {
                InstituicaoBancaria instituicao = instituicoes.get(conta.getBancoId());
                if (instituicao != null) {
                    instituicao.adicionarConta(conta);
                }
            }
        }
    }

    public Conta getConta(int bancoId, int contaId) {
        InstituicaoBancaria instituicao = instituicoes.get(bancoId);
        return (instituicao != null) ? instituicao.getConta(contaId) : null;
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
     * Cria uma nova conta na instituicao informada (usado pela tela de
     * Cadastro da interface JavaFX).
     */
    public Conta criarConta(int bancoId, String nome, String senha) {
        InstituicaoBancaria instituicao = instituicoes.get(bancoId);
        if (instituicao == null) {
            return null;
        }
        Conta conta = instituicao.criarConta(nome, senha);
        dao.inserir(conta);
        return conta;
    }

    public boolean transferir(int bancoOrigemId, int idOrigem, int bancoDestinoId, int idDestino, double valor) {

        Conta contaOrigem = getConta(bancoOrigemId, idOrigem);
        Conta contaDestino = getConta(bancoDestinoId, idDestino);

        if (contaOrigem == null || contaDestino == null
                || (bancoOrigemId == bancoDestinoId && idOrigem == idDestino)
                || valor <= 0) {
            return false;
        }

        // PREVENCAO DE DEADLOCK: Ordenacao de Recursos por (bancoId, contaId).
        // Compara primeiro o banco; so usa o ID da conta como desempate
        // quando as duas contas pertencem ao mesmo banco. Isso e necessario
        // porque, com multiplos bancos, IDs de conta podem se repetir entre
        // instituicoes diferentes - ordenar so por ID de conta (como antes)
        // deixaria de ser uma ordem valida e reabriria a possibilidade de
        // deadlock/espera circular.
        boolean origemPrimeiro;
        if (contaOrigem.getBancoId() != contaDestino.getBancoId()) {
            origemPrimeiro = contaOrigem.getBancoId() < contaDestino.getBancoId();
        } else {
            origemPrimeiro = contaOrigem.getId() < contaDestino.getId();
        }

        Conta primeiraConta = origemPrimeiro ? contaOrigem : contaDestino;
        Conta segundaConta = origemPrimeiro ? contaDestino : contaOrigem;

        primeiraConta.getLock().lock();
        try {
            segundaConta.getLock().lock();
            try {
                if (contaOrigem.getSaldo() >= valor) {
                    contaOrigem.sacar(valor);
                    contaDestino.depositar(valor);

                    dao.atualizar(contaOrigem);
                    dao.atualizar(contaDestino);

                    contaOrigem.registrarTransferencia(
                            String.format("Transferencia enviada para banco %d conta %d: R$ %.2f", bancoDestinoId, idDestino, valor));
                    contaDestino.registrarTransferencia(
                            String.format("Transferencia recebida do banco %d conta %d: R$ %.2f", bancoOrigemId, idOrigem, valor));
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