package com.insidebank.controller;

import java.io.IOException;

import com.insidebank.MainApp;
import com.insidebank.model.Sessao;
import com.insidebank.net.BankClient;
import com.insidebank.net.Resultado;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller da tela de Configuracoes. O painel "Geral" e' totalmente
 * funcional (troca de tema claro/escuro persistida em Sessao, ver
 * MainApp.aplicarTemaNaCenaAtual()); os demais paineis sao informativos.
 */
public class ConfiguracoesController {

    @FXML private Button pillGeral;
    @FXML private Button pillMinhaConta;
    @FXML private Button pillPrivacidade;
    @FXML private Button pillAjuda;

    @FXML private VBox painelGeral;
    @FXML private VBox painelMinhaConta;
    @FXML private VBox painelPrivacidade;
    @FXML private VBox painelAjuda;

    @FXML private Button btnTemaEscuro;
    @FXML private Button btnTemaClaro;

    @FXML private Label lblNumeroConta;
    @FXML private Label lblNomeConta;

    @FXML
    public void initialize() {
        atualizarBotoesTema();
    }

    @FXML
    public void onShown() {
        // chamado apos a tela ser carregada via MainApp.trocarTela (inicialize ja rodou)
        String pedido = com.insidebank.model.Sessao.getInstance().getAbrirConfiguracoesPainel();
        if (pedido != null) {
            switch (pedido) {
                case "minhaConta": onMostrarMinhaConta(); break;
                case "privacidade": onMostrarPrivacidade(); break;
                case "ajuda": onMostrarAjuda(); break;
                default: onMostrarGeral(); break;
            }
            com.insidebank.model.Sessao.getInstance().setAbrirConfiguracoesPainel(null);
        }
    }

    // ===================== Navegacao entre paineis =====================

    @FXML
    private void onMostrarGeral() {
        selecionarPainel(painelGeral, pillGeral);
    }

    @FXML
    private void onMostrarMinhaConta() {
        selecionarPainel(painelMinhaConta, pillMinhaConta);
        carregarMinhaConta();
    }

    @FXML
    private void onMostrarPrivacidade() {
        selecionarPainel(painelPrivacidade, pillPrivacidade);
    }

    @FXML
    private void onMostrarAjuda() {
        selecionarPainel(painelAjuda, pillAjuda);
    }

    private void selecionarPainel(VBox painel, Button pill) {
        for (VBox p : new VBox[]{painelGeral, painelMinhaConta, painelPrivacidade, painelAjuda}) {
            p.setVisible(p == painel);
            p.setManaged(p == painel);
        }
        for (Button b : new Button[]{pillGeral, pillMinhaConta, pillPrivacidade, pillAjuda}) {
            b.getStyleClass().remove("nav-pill-active");
        }
        if (!pill.getStyleClass().contains("nav-pill-active")) {
            pill.getStyleClass().add("nav-pill-active");
        }
    }

    // ===================== Minha Conta =====================

    private void carregarMinhaConta() {
        int meuBancoId = Sessao.getInstance().getContaId();
        int minhaContaId = Sessao.getInstance().getContaId();
        lblNumeroConta.setText(String.valueOf(minhaContaId));

        BankClient client = Sessao.getInstance().getClient();
        if (client == null) {
            return;
        }
        Task<Resultado> tarefa = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                return client.consultarNome(meuBancoId, minhaContaId);
            }
        };
        tarefa.setOnSucceeded(evt -> {
            Resultado r = tarefa.getValue();
            lblNomeConta.setText(r.isSucesso() ? r.getMensagem() : "--");
        });
        new Thread(tarefa, "minha-conta-task").start();
    }

    // ===================== Tema =====================

    @FXML
    private void onTemaEscuro() {
        Sessao.getInstance().setTema("dark");
        MainApp.aplicarTemaNaCenaAtual();
        atualizarBotoesTema();
    }

    @FXML
    private void onTemaClaro() {
        Sessao.getInstance().setTema("light");
        MainApp.aplicarTemaNaCenaAtual();
        atualizarBotoesTema();
    }

    private void atualizarBotoesTema() {
        boolean claro = "light".equals(Sessao.getInstance().getTema());
        btnTemaClaro.getStyleClass().removeAll("btn-primary-pill", "btn-glass-pill");
        btnTemaEscuro.getStyleClass().removeAll("btn-primary-pill", "btn-glass-pill");
        btnTemaClaro.getStyleClass().add(claro ? "btn-primary-pill" : "btn-glass-pill");
        btnTemaEscuro.getStyleClass().add(claro ? "btn-glass-pill" : "btn-primary-pill");
    }

    // ===================== Navegacao entre telas =====================

    @FXML
    private void onVoltarDashboard() {
        trocar("/fxml/dashboard.fxml");
    }

    @FXML
    private void onFechar() {
        trocar("/fxml/dashboard.fxml");
    }

    @FXML
    private void onAbrirExtrato() {
        // Extrato agora e' um modal sobre o Dashboard (ver DashboardController.onNavExtrato) -
        // a partir de Configuracoes, simplesmente volta ao Dashboard.
        trocar("/fxml/dashboard.fxml");
    }

    private void trocar(String fxml) {
        try {
            MainApp.trocarTela(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
