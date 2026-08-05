package com.insidebank.controller;

import com.insidebank.model.BancoOption;
import com.insidebank.model.Sessao;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller do modal 1/3 do fluxo de Transferencia (transferir_destino.fxml).
 * Recolhe banco + conta de destino + valor e, se validos, abre o modal de confirmacao.
 */
public class TransferirDestinoController {

    @FXML private ComboBox<BancoOption> cmbBanco;
    @FXML private TextField txtDestino;
    @FXML private TextField txtValor;
    @FXML private Label lblErro;
    @FXML private Button btnFechar;

    private DashboardController dashboard;

    @FXML
    private void initialize() {
        cmbBanco.getItems().addAll(BancoOption.BANCOS_DISPONIVEIS);
        cmbBanco.getSelectionModel().selectFirst();

        if (btnFechar != null) {
            btnFechar.setOnAction(event -> onFechar());
            btnFechar.setOnMouseClicked(event -> onFechar());
        }
    }

    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    // Atalhos assumem Banco Alpha (id 1), onde vivem as contas de teste fixas.
    private void selecionarBancoAlpha() {
        for (BancoOption opcao : cmbBanco.getItems()) {
            if (opcao.getId() == 1) {
                cmbBanco.getSelectionModel().select(opcao);
                break;
            }
        }
    }

    @FXML
    private void onSelecionarConta1() {
        selecionarBancoAlpha();
        txtDestino.setText("1");
    }

    @FXML
    private void onSelecionarConta2() {
        selecionarBancoAlpha();
        txtDestino.setText("2");
    }

    @FXML
    private void onSelecionarConta3() {
        selecionarBancoAlpha();
        txtDestino.setText("3");
    }

    @FXML
    private void onFechar() {
        dashboard.fecharModalAtual();
    }

    @FXML
    private void onContinuar() {
        esconderErro();

        BancoOption bancoSelecionado = cmbBanco.getValue();
        if (bancoSelecionado == null) {
            mostrarErro("Selecione o banco de destino.");
            return;
        }

        int destino;
        try {
            destino = Integer.parseInt(txtDestino.getText().trim());
        } catch (NumberFormatException e) {
            mostrarErro("Informe um numero de conta valido.");
            return;
        }

        int bancoDestino = bancoSelecionado.getId();

        if (bancoDestino == Sessao.getInstance().getBancoId()
                && destino == Sessao.getInstance().getContaId()) {
            mostrarErro("Nao e possivel transferir para a propria conta.");
            return;
        }

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

        TransferirConfirmarController confirmar = dashboard.abrirModal("/fxml/transferir_confirmar.fxml");
        if (confirmar != null) {
            confirmar.configurar(dashboard, bancoDestino, destino, valor);
        }
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