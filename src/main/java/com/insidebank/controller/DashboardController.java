package com.insidebank.controller;

import java.io.IOException;

import com.insidebank.MainApp;
import com.insidebank.model.Sessao;
import com.insidebank.net.BankClient;
import com.insidebank.net.Resultado;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Controller do Dashboard (Figma: "Dashboard 1", node 131:40).
 *
 * Alem do conteudo fixo da tela, este controller gerencia um sistema simples
 * de "modal por cima do dashboard" (ver abrirModal/fecharModalAtual), que
 * reproduz o padrao visto no Figma para o fluxo de Transferencia (fundo
 * escurecido + card central) e e reaproveitado por Deposito e Saque.
 */
public class DashboardController {

    @FXML private Pane rootPane;
    @FXML private Label lblSaldo;

    @FXML private Button btnNavHome;
    @FXML private Button btnNavTransferir;
    @FXML private Button btnNavExtrato;
    @FXML private Button btnNavConfiguracoes;
    @FXML private Button btnNavSair;

    private Node modalAberto;

    @FXML
    public void initialize() {
        atualizarSaldo();
    }

    // ===================== Saldo =====================

    public void atualizarSaldo() {
        BankClient client = Sessao.getInstance().getClient();
        if (client == null || !client.isConectado()) {
            lblSaldo.setText("R$ --");
            return;
        }

        Task<Resultado> tarefaSaldo = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                return client.saldo();
            }
        };

        tarefaSaldo.setOnSucceeded(evt -> {
            Resultado r = tarefaSaldo.getValue();
            if (r.isSucesso()) {
                // mensagem vem como "Saldo: 1000.0" (ver AtendimentoCliente.java)
                String valor = r.getMensagem().replace("Saldo:", "").trim();
                lblSaldo.setText(formatarMoeda(valor));
            } else {
                lblSaldo.setText("R$ --");
            }
        });
        tarefaSaldo.setOnFailed(evt -> lblSaldo.setText("R$ --"));

        new Thread(tarefaSaldo, "saldo-task").start();
    }

    public static String formatarMoeda(String valorBruto) {
        try {
            double valor = Double.parseDouble(valorBruto.replace(",", "."));
            return String.format("R$ %,.2f", valor).replace(',', '#').replace('.', ',').replace('#', '.');
        } catch (NumberFormatException e) {
            return "R$ " + valorBruto;
        }
    }

    // ===================== Sistema de modais =====================

    /**
     * Carrega um FXML e o exibe por cima do dashboard (o proprio FXML do modal
     * e responsavel por desenhar o fundo escurecido, ver transferir-destino.fxml
     * como referencia). Fecha automaticamente qualquer modal ja aberto antes.
     *
     * @return o controller do FXML carregado, para quem chamou poder configurar
     *         dados iniciais ou callbacks.
     */
    public <T> T abrirModal(String caminhoFxml) {
        try {
            fecharModalAtual();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent modalRoot = loader.load();
            modalAberto = modalRoot;
            rootPane.getChildren().add(modalRoot);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void fecharModalAtual() {
        if (modalAberto != null) {
            rootPane.getChildren().remove(modalAberto);
            modalAberto = null;
        }
    }

    // ===================== Navegacao =====================

    @FXML
    private void onNavHome() {
        fecharModalAtual();
        atualizarSaldo();
    }

    @FXML
    private void onNavTransferir() {
        TransferirDestinoController c = abrirModal("/fxml/transferir_destino.fxml");
        if (c != null) {
            c.setDashboard(this);
        }
    }

    @FXML
    private void onNavExtrato() {
        ExtratoController c = abrirModal("/fxml/extrato.fxml");
        if (c != null) {
            c.setDashboard(this);
        }
    }

    @FXML
    private void onNavDeposito() {
        ValorTransacaoController c = abrirModal("/fxml/valor_transacao.fxml");
        if (c != null) {
            c.configurar(this, ValorTransacaoController.Modo.DEPOSITO);
        }
    }

    @FXML
    private void onNavSacar() {
        ValorTransacaoController c = abrirModal("/fxml/valor_transacao.fxml");
        if (c != null) {
            c.configurar(this, ValorTransacaoController.Modo.SAQUE);
        }
    }

    @FXML
    private void onNavConfiguracoes() {
        try {
            MainApp.trocarTela("/fxml/configuracoes.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onProfileClicked() {
        // Ao clicar no icone de perfil, abrir Configuracoes na aba "Minha Conta"
        com.insidebank.model.Sessao.getInstance().setAbrirConfiguracoesPainel("minhaConta");
        try {
            MainApp.trocarTela("/fxml/configuracoes.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSair() {
        BankClient client = Sessao.getInstance().getClient();
        if (client == null) {
            voltarParaLogin();
            return;
        }

        Task<Void> tarefaLogout = new Task<>() {
            @Override
            protected Void call() {
                try {
                    client.logout();
                } catch (IOException e) {
                    // encerrando de qualquer forma
                }
                return null;
            }
        };
        tarefaLogout.setOnSucceeded(evt -> Platform.runLater(this::finalizarSessaoEVoltar));
        tarefaLogout.setOnFailed(evt -> Platform.runLater(this::finalizarSessaoEVoltar));
        new Thread(tarefaLogout, "logout-task").start();
    }

    private void finalizarSessaoEVoltar() {
        Sessao.getInstance().encerrar();
        voltarParaLogin();
    }

    private void voltarParaLogin() {
        try {
            MainApp.trocarTela("/fxml/login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void avisoTelaPendente(String nomeAmigavel, String referenciaFigma) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(nomeAmigavel);
        alert.setHeaderText("Tela ainda nao implementada nesta etapa");
        alert.setContentText("Referencia no Figma: " + referenciaFigma
                + "\n\nVer ROADMAP_TELAS.md.");
        alert.showAndWait();
    }
}
