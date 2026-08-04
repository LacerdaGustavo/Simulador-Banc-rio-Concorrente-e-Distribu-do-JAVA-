package com.insidebank.controller;

import com.insidebank.model.Sessao;
import com.insidebank.net.BankClient;
import com.insidebank.net.Resultado;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controller compartilhado do modal de Deposito/Saque (valor_transacao.fxml).
 * Ver comentario no FXML sobre a ausencia de uma tela dedicada de Saque no Figma.
 */
public class ValorTransacaoController {

    public enum Modo {
        DEPOSITO("Depositar", "fth-download"),
        SAQUE("Sacar", "fth-minus-circle");

        final String rotulo;
        final String icone;

        Modo(String rotulo, String icone) {
            this.rotulo = rotulo;
            this.icone = icone;
        }
    }

    @FXML private FontIcon iconeModo;
    @FXML private Label lblTitulo;
    @FXML private Label lblErro;
    @FXML private TextField txtValor;
    @FXML private Button btnConfirmar;
    @FXML private Button btnFechar;

    private DashboardController dashboard;

    @FXML
    private void initialize() {
        if (btnFechar != null) {
            btnFechar.setOnAction(event -> onFechar());
            btnFechar.setOnMouseClicked(event -> onFechar());
        }
    }
    private Modo modo;

    public void configurar(DashboardController dashboard, Modo modo) {
        this.dashboard = dashboard;
        this.modo = modo;
        lblTitulo.setText(modo.rotulo);
        btnConfirmar.setText(modo.rotulo);
        iconeModo.setIconLiteral(modo.icone);
    }

    @FXML
    private void onFechar() {
        dashboard.fecharModalAtual();
    }

    @FXML
    private void onConfirmar() {
        esconderErro();

        double valor;
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            mostrarErro("Informe um valor valido.");
            return;
        }
        if (valor <= 0) {
            mostrarErro("O valor deve ser maior que zero.");
            return;
        }

        btnConfirmar.setDisable(true);
        BankClient client = Sessao.getInstance().getClient();
        double valorFinal = valor;

        Task<Resultado> tarefa = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                return modo == Modo.DEPOSITO ? client.depositar(valorFinal) : client.sacar(valorFinal);
            }
        };

        tarefa.setOnSucceeded(evt -> {
            Resultado r = tarefa.getValue();
            btnConfirmar.setDisable(false);
            if (r.isSucesso()) {
                dashboard.fecharModalAtual();
                dashboard.atualizarSaldo();
            } else {
                mostrarErro(r.getMensagem());
            }
        });
        tarefa.setOnFailed(evt -> {
            btnConfirmar.setDisable(false);
            mostrarErro("Erro de comunicacao com o servidor.");
        });

        new Thread(tarefa, "valor-transacao-task").start();
    }

    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void esconderErro() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }
}
