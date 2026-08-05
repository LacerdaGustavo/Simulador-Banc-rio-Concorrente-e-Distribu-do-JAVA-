package com.insidebank.controller;

import java.io.IOException;

import com.insidebank.MainApp;
import com.insidebank.model.BancoOption;
import com.insidebank.net.BankClient;
import com.insidebank.net.Resultado;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller da tela de Cadastro. Usa o comando novo CADASTRAR (ver
 * AtendimentoCliente.java / Banco.criarConta) para criar uma conta de
 * verdade no servidor, na instituicao escolhida - nao precisa estar
 * logado para isso.
 */
public class CadastroController {

    private static final String SERVIDOR_HOST = "127.0.0.1";
    private static final int SERVIDOR_PORTA = 4444;

    @FXML private ComboBox<BancoOption> cmbBanco;
    @FXML private TextField txtNome;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmarSenha;
    @FXML private Label lblErro;
    @FXML private Button btnCriarConta;

    @FXML
    private void initialize() {
        cmbBanco.getItems().addAll(BancoOption.BANCOS_DISPONIVEIS);
        cmbBanco.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCriarConta() {
        esconderErro();

        BancoOption bancoSelecionado = cmbBanco.getValue();
        if (bancoSelecionado == null) {
            mostrarErro("Selecione o banco.");
            return;
        }

        String nome = txtNome.getText().trim();
        String senha = txtSenha.getText();
        String confirmacao = txtConfirmarSenha.getText();

        if (nome.isEmpty()) {
            mostrarErro("Informe o nome completo.");
            return;
        }
        if (senha == null || senha.length() < 4) {
            mostrarErro("A senha deve ter pelo menos 4 caracteres.");
            return;
        }
        if (!senha.equals(confirmacao)) {
            mostrarErro("As senhas nao coincidem.");
            return;
        }

        int bancoId = bancoSelecionado.getId();
        btnCriarConta.setDisable(true);

        Task<Resultado> tarefa = new Task<>() {
            @Override
            protected Resultado call() throws Exception {
                BankClient client = new BankClient();
                client.conectar(SERVIDOR_HOST, SERVIDOR_PORTA);
                Resultado resultado = client.cadastrar(bancoId, nome, senha);
                client.fechar(); // cadastro nao mantem sessao aberta - o usuario faz LOGIN em seguida
                return resultado;
            }
        };

        tarefa.setOnSucceeded(evt -> {
            btnCriarConta.setDisable(false);
            Resultado r = tarefa.getValue();
            if (r.isSucesso()) {
                mostrarSucessoEVoltar(r.getMensagem());
            } else {
                mostrarErro(r.getMensagem());
            }
        });
        tarefa.setOnFailed(evt -> {
            btnCriarConta.setDisable(false);
            mostrarErro("Nao foi possivel conectar ao servidor em "
                    + SERVIDOR_HOST + ":" + SERVIDOR_PORTA + ".");
        });

        new Thread(tarefa, "cadastro-task").start();
    }

    private void mostrarSucessoEVoltar(String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Conta criada");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
        onJaTenhoConta();
    }

    @FXML
    private void onJaTenhoConta() {
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