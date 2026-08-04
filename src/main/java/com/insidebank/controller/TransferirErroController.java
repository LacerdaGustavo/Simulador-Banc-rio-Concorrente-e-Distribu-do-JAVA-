package com.insidebank.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Controller do modal 3/3 do fluxo de Transferencia (transferir_erro.fxml). */
public class TransferirErroController {

    @FXML private Label lblMensagem;

    private DashboardController dashboard;

    public void configurar(DashboardController dashboard, String mensagem) {
        this.dashboard = dashboard;
        if (mensagem != null && !mensagem.isBlank()) {
            lblMensagem.setText(mensagem);
        }
    }

    @FXML
    private void onFechar() {
        dashboard.fecharModalAtual();
        dashboard.atualizarSaldo();
    }
}
