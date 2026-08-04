package com.insidebank.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.insidebank.model.Sessao;
import com.insidebank.net.BankClient;
import com.insidebank.net.Resultado;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controller do modal de Extrato (extrato.fxml, Figma node 202:6063).
 * Busca o historico real via o comando novo EXTRATO (ver AtendimentoCliente.java
 * / Conta.getHistorico()) e transforma cada linha bruta (ex: "01/08 12:34:56 -
 * Deposito de R$ 500,00 (saldo: R$ 1500,00)") em uma linha visual com icone,
 * descricao e valor, no mesmo estilo das linhas "Pix Enviado/Recebido" do Figma.
 */
public class ExtratoController {

    private static final Pattern PADRAO_LINHA = Pattern.compile(
            "^(\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}) - (.+)$");
    private static final Pattern PADRAO_VALOR = Pattern.compile("R\\$ ([0-9.,]+)");

    @FXML private VBox listaMovimentacoes;
    @FXML private Button btnFechar;

    private DashboardController dashboard;

    @FXML
    private void initialize() {
        if (btnFechar != null) {
            btnFechar.setOnAction(event -> onFechar());
            btnFechar.setOnMouseClicked(event -> onFechar());
        }
    }

    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
        carregarExtrato();
    }

    private void carregarExtrato() {
        BankClient client = Sessao.getInstance().getClient();
        if (client == null) {
            adicionarMensagem("Sessao invalida - faca login novamente.");
            return;
        }

        Task<Resultado> tarefa = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                return client.extrato();
            }
        };

        tarefa.setOnSucceeded(evt -> {
            Resultado r = tarefa.getValue();
            listaMovimentacoes.getChildren().clear();
            if (!r.isSucesso()) {
                adicionarMensagem("Nao foi possivel carregar o extrato: " + r.getMensagem());
                return;
            }
            String mensagem = r.getMensagem();
            if (mensagem.isBlank() || mensagem.equals("Nenhuma movimentacao registrada")) {
                adicionarMensagem("Nenhuma movimentacao registrada ainda.");
                return;
            }
            for (String linha : mensagem.split("\\|")) {
                adicionarLinha(linha);
            }
        });
        tarefa.setOnFailed(evt -> adicionarMensagem("Erro de comunicacao com o servidor."));

        new Thread(tarefa, "extrato-task").start();
    }

    /** Interpreta uma linha bruta do historico e monta a linha visual correspondente. */
    private void adicionarLinha(String linhaBruta) {
        String data = "";
        String resto = linhaBruta;
        Matcher mData = PADRAO_LINHA.matcher(linhaBruta);
        if (mData.matches()) {
            data = mData.group(1);
            resto = mData.group(2);
        }

        String descricao;
        String icone;
        String corIcone;
        boolean entrada; // true = dinheiro entrando (verde), false = saindo (vermelho)

        String restoLower = resto.toLowerCase();
        if (restoLower.startsWith("deposito")) {
            descricao = "Deposito";
            icone = "fth-arrow-down-left";
            entrada = true;
        } else if (restoLower.startsWith("saque")) {
            descricao = "Saque";
            icone = "fth-arrow-up-right";
            entrada = false;
        } else if (restoLower.startsWith("transferencia enviada")) {
            descricao = resto.replaceFirst("(?i)transferencia enviada para conta (\\d+).*", "Transferencia enviada (conta $1)");
            icone = "fth-arrow-up-right";
            entrada = false;
        } else if (restoLower.startsWith("transferencia recebida")) {
            descricao = resto.replaceFirst("(?i)transferencia recebida da conta (\\d+).*", "Transferencia recebida (conta $1)");
            icone = "fth-arrow-down-left";
            entrada = true;
        } else {
            descricao = resto;
            icone = "fth-file-text";
            entrada = true;
        }
        corIcone = entrada ? "#3DDC97" : "#FF6B6B";

        String valorFormatado = "";
        Matcher mValor = PADRAO_VALOR.matcher(resto);
        if (mValor.find()) {
            valorFormatado = DashboardController.formatarMoeda(mValor.group(1));
        }

        HBox linha = new HBox(16);
        linha.setPrefWidth(628);
        linha.setPrefHeight(64);
        linha.setStyle("-fx-alignment: center-left;");

        Pane circulo = new Pane();
        circulo.setPrefSize(64, 64);
        circulo.setStyle("-fx-background-color:#191919; -fx-background-radius:100; -fx-border-color:#303030; -fx-border-radius:100;");
        FontIcon fontIcon = new FontIcon(icone);
        fontIcon.setIconSize(22);
        fontIcon.setIconColor(javafx.scene.paint.Color.web(corIcone));
        fontIcon.setLayoutX(21);
        fontIcon.setLayoutY(21);
        circulo.getChildren().add(fontIcon);

        VBox textos = new VBox(2);
        Label lblDescricao = new Label(descricao);
        lblDescricao.setStyle("-fx-text-fill:white; -fx-font-size:15px;");
        Label lblData = new Label(data);
        lblData.setStyle("-fx-text-fill:rgba(255,255,255,0.4); -fx-font-size:12px;");
        textos.getChildren().addAll(lblDescricao, lblData);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Label lblValor = new Label((entrada ? "+ " : "- ") + valorFormatado);
        lblValor.setStyle("-fx-text-fill:" + corIcone + "; -fx-font-size:16px;");

        linha.getChildren().addAll(circulo, textos, lblValor);
        listaMovimentacoes.getChildren().add(linha);
    }

    private void adicionarMensagem(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-text-fill:rgba(255,255,255,0.5); -fx-font-size:14px;");
        label.setPadding(new Insets(20, 0, 0, 0));
        listaMovimentacoes.getChildren().add(label);
    }

    @FXML
    private void onFechar() {
        if (dashboard != null) {
            dashboard.fecharModalAtual();
        }
    }
}
