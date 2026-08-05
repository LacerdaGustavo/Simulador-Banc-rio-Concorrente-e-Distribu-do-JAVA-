package com.insidebank.controller;

import java.io.IOException;

import com.insidebank.MainApp;
import com.insidebank.model.BancoOption;
import com.insidebank.model.Sessao;
import com.insidebank.net.BankClient;
import com.insidebank.net.Resultado;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private static final String SERVIDOR_HOST = "127.0.0.1";
    private static final int SERVIDOR_PORTA = 4444;

    @FXML private ComboBox<BancoOption> cmbBanco;
    @FXML private TextField txtConta;
    @FXML private PasswordField txtSenha;
    @FXML private CheckBox chkConfiar;
    @FXML private Button btnLogin;
    @FXML private Label lblErro;

    @FXML
    private void initialize() {
        cmbBanco.getItems().addAll(BancoOption.BANCOS_DISPONIVEIS);
        cmbBanco.getSelectionModel().selectFirst();
    }

    @FXML
    private void onFazerLogin() {
        esconderErro();

        BancoOption bancoSelecionado = cmbBanco.getValue();
        if (bancoSelecionado == null) {
            mostrarErro("Selecione o banco.");
            return;
        }

        int contaId;
        try {
            contaId = Integer.parseInt(txtConta.getText().trim());
        } catch (NumberFormatException e) {
            mostrarErro("Informe o numero da conta (ex: 1, 2 ou 3 nas contas de teste).");
            return;
        }
        String senha = txtSenha.getText();
        if (senha == null || senha.isEmpty()) {
            mostrarErro("Informe a senha.");
            return;
        }

        int bancoId = bancoSelecionado.getId();
        btnLogin.setDisable(true);

        Task<Resultado> tarefaLogin = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                BankClient client = new BankClient();
                client.conectar(SERVIDOR_HOST, SERVIDOR_PORTA);
                Resultado resultado = client.login(bancoId, contaId, senha);
                if (resultado.isSucesso()) {
                    Sessao.getInstance().setClient(client);
                    Sessao.getInstance().setBancoId(bancoId);
                    Sessao.getInstance().setContaId(contaId);
                } else {
                    client.fechar();
                }
                return resultado;
            }
        };

        tarefaLogin.setOnSucceeded(evt -> {
            btnLogin.setDisable(false);
            Resultado resultado = tarefaLogin.getValue();
            if (resultado.isSucesso()) {
                irPara2FA();
            } else {
                mostrarErro(resultado.getMensagem());
            }
        });

        tarefaLogin.setOnFailed(evt -> {
            btnLogin.setDisable(false);
            mostrarErro("Nao foi possivel conectar ao servidor em "
                    + SERVIDOR_HOST + ":" + SERVIDOR_PORTA
                    + ". Verifique se o Servidor.java esta em execucao.");
        });

        new Thread(tarefaLogin, "login-task").start();
    }

    @FXML
    private void onCadastrarSe() {
        try {
            MainApp.trocarTela("/fxml/cadastro.fxml");
        } catch (IOException e) {
            mostrarErro("Erro ao carregar a tela de cadastro: " + e.getMessage());
        }
    }

    private void irPara2FA() {
        try {
            MainApp.trocarTela("/fxml/login_2fa.fxml");
        } catch (IOException e) {
            mostrarErro("Erro ao carregar a verificacao: " + e.getMessage());
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