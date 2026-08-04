package com.insidebank.controller;

import java.io.IOException;
import java.security.SecureRandom;

import com.insidebank.MainApp;
import com.insidebank.model.Sessao;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller da tela de Login 2FA. Ver nota no login_2fa.fxml sobre o codigo
 * de demonstracao (nao ha integracao real de SMS/e-mail neste projeto).
 */
public class Login2FAController {

    private static final SecureRandom RANDOM = new SecureRandom();

    @FXML private Label lblCodigoDemo;
    @FXML private TextField txtCodigo;
    @FXML private Label lblErro;
    @FXML private Button btnVerificar;

    private String codigoGerado;

    @FXML
    public void initialize() {
        codigoGerado = String.format("%06d", RANDOM.nextInt(1_000_000));
        lblCodigoDemo.setText(codigoGerado);
    }

    @FXML
    private void onVerificar() {
        esconderErro();
        String digitado = txtCodigo.getText().trim();
        if (digitado.equals(codigoGerado)) {
            try {
                MainApp.trocarTela("/fxml/dashboard.fxml");
            } catch (IOException e) {
                mostrarErro("Erro ao carregar o Dashboard: " + e.getMessage());
            }
        } else {
            mostrarErro("Codigo incorreto. Confira o codigo de demonstracao acima.");
        }
    }

    @FXML
    private void onCancelar() {
        // O login ja tinha sido aceito pelo servidor antes desta tela (ver
        // LoginController) - como o usuario desistiu da verificacao, encerra
        // a conexao/sessao e volta ao Login.
        Sessao.getInstance().encerrar();
        try {
            MainApp.trocarTela("/fxml/login.fxml");
        } catch (IOException e) {
            mostrarErro("Erro ao voltar para o login: " + e.getMessage());
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
