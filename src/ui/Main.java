package ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream saida;


    @Override
    public void start(Stage stage) {

    

        try {

            socket = new Socket("localhost", 4444);

            entrada = new DataInputStream(socket.getInputStream());

            saida = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {

            System.out.println("Erro ao conectar ao servidor.");
            e.printStackTrace();
            return;

        }

        // Título
        Label titulo = new Label("Sistema Bancário");

        // Campo da conta
        TextField campoConta = new TextField();
        campoConta.setPromptText("Digite o número da conta");

        // Campo da senha
        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Digite a senha");

        // Botão
        Button btnEntrar = new Button("Entrar");

        // Label para mostrar mensagens
        Label lblMensagem = new Label("");

        // Evento do botão
        btnEntrar.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

            String conta = campoConta.getText();
            String senha = campoSenha.getText();

            try {

                String comando = "LOGIN;" + conta + ";" + senha;

                saida.writeUTF(comando);

                String resposta = entrada.readUTF();

                lblMensagem.setText(resposta);

            } catch (IOException e) {

                e.printStackTrace();

            }

            }

        });

        // Layout
        VBox layout = new VBox();

        layout.setSpacing(15);

        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(
                titulo,
                campoConta,
                campoSenha,
                btnEntrar,
                lblMensagem
        );

        // Cena
        Scene scene = new Scene(layout, 400, 300);

        // Janela
        stage.setTitle("Sistema Bancário");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}