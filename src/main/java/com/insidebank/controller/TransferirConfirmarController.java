package com.insidebank.controller;

import com.insidebank.model.Sessao;
import com.insidebank.net.BankClient;
import com.insidebank.net.Resultado;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller do modal 2/3 do fluxo de Transferencia (transferir_confirmar.fxml).
 * Busca o nome da conta de destino (comando NOME), exibe o resumo e, ao
 * confirmar, chama de fato o comando TRANSF do servidor.
 */
public class TransferirConfirmarController {

    @FXML private Label lblNomeDestino;
    @FXML private Label lblContaDestino;
    @FXML private Label lblValor;
    @FXML private Label lblErro;
    @FXML private Button btnConfirmar;

    private DashboardController dashboard;
    private int bancoDestino;
    private int contaDestino;
    private double valor;

    public void configurar(DashboardController dashboard, int bancoDestino, int contaDestino, double valor) {
        this.dashboard = dashboard;
        this.bancoDestino = bancoDestino;
        this.contaDestino = contaDestino;
        this.valor = valor;

        lblContaDestino.setText("Banco " + bancoDestino + ", Conta " + contaDestino);
        lblValor.setText(DashboardController.formatarMoeda(String.valueOf(valor)));

        BankClient client = Sessao.getInstance().getClient();
        Task<Resultado> tarefaNome = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                return client.consultarNome(bancoDestino, contaDestino);
            }
        };
        tarefaNome.setOnSucceeded(evt -> {
            Resultado r = tarefaNome.getValue();
            lblNomeDestino.setText(r.isSucesso() ? r.getMensagem() : "Cliente " + contaDestino);
        });
        tarefaNome.setOnFailed(evt -> lblNomeDestino.setText("Cliente " + contaDestino));
        new Thread(tarefaNome, "consulta-nome-task").start();
    }

    @FXML
    private void onCancelar() {
        dashboard.fecharModalAtual();
    }

    @FXML
    private void onConfirmar() {
        btnConfirmar.setDisable(true);
        BankClient client = Sessao.getInstance().getClient();

        Task<Resultado> tarefaTransf = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                return client.transferir(bancoDestino, contaDestino, valor);
            }
        };

        tarefaTransf.setOnSucceeded(evt -> {
            Resultado r = tarefaTransf.getValue();
            if (r.isSucesso()) {
                dashboard.fecharModalAtual();
                dashboard.atualizarSaldo();
            } else {
                abrirErro(r.getMensagem());
            }
        });
        tarefaTransf.setOnFailed(evt -> abrirErro("Erro de comunicacao com o servidor."));

        new Thread(tarefaTransf, "transferencia-task").start();
    }

    private void abrirErro(String mensagem) {
        TransferirErroController erro = dashboard.abrirModal("/fxml/transferir_erro.fxml");
        if (erro != null) {
            erro.configurar(dashboard, mensagem);
        }
    }
}