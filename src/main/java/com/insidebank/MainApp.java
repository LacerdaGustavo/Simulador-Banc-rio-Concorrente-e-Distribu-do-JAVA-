package com.insidebank;

import java.io.IOException;

import com.insidebank.model.Sessao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static final double LARGURA_DESIGN = 1440;
    public static final double ALTURA_JANELA = 1100;

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;
        stage.setTitle("InsideBank");
        stage.setResizable(true);
        stage.setMinWidth(LARGURA_DESIGN);
        stage.setMaxWidth(LARGURA_DESIGN);
        stage.setMinHeight(ALTURA_JANELA);
        stage.setMaxHeight(ALTURA_JANELA);

        trocarTela("/fxml/login.fxml", "/css/theme-dark.css");

        stage.setWidth(LARGURA_DESIGN);
        stage.setHeight(ALTURA_JANELA);
        stage.centerOnScreen();
        stage.show();
    }

    public static void trocarTela(String caminhoFxml, String caminhoCss) throws IOException {
        if (stagePrincipal == null) {
            return;
        }

        boolean telaCheia = stagePrincipal.isFullScreen();
        boolean maximizada = stagePrincipal.isMaximized();
        double largura = stagePrincipal.getWidth();
        double altura = stagePrincipal.getHeight();
        double x = stagePrincipal.getX();
        double y = stagePrincipal.getY();

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(caminhoFxml));
        Parent root = loader.load();
        // se o controller expor um metodo onShown(), invoque-o para permitir hooks apos troca de tela
        try {
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("onShown").invoke(controller);
                } catch (NoSuchMethodException ignored) {
                    // controller nao fornece onShown, nada a fazer
                }
            }
        } catch (Exception e) {
            // nao interrompa a troca de tela por causa deste callback
        }

        Scene scene = stagePrincipal.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stagePrincipal.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add(MainApp.class.getResource(caminhoCss).toExternalForm());

        if (telaCheia) {
            stagePrincipal.setFullScreen(true);
        } else {
            if (largura > 0 && altura > 0) {
                stagePrincipal.setWidth(largura);
                stagePrincipal.setHeight(altura);
            }
            if (x != 0 || y != 0) {
                stagePrincipal.setX(x);
                stagePrincipal.setY(y);
            }
        }

        stagePrincipal.setMaximized(maximizada);
    }

    public static void trocarTela(String caminhoFxml) throws IOException {
        trocarTela(caminhoFxml, cssAtual());
    }

    public static String cssAtual() {
        return "light".equals(Sessao.getInstance().getTema()) ? "/css/theme-light.css" : "/css/theme-dark.css";
    }

    public static void aplicarTemaNaCenaAtual() {
        Scene scene = stagePrincipal.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(MainApp.class.getResource(cssAtual()).toExternalForm());
    }

    public static Stage getStage() {
        return stagePrincipal;
    }

    public static void main(String[] args) {
        launch(args);
    }
}