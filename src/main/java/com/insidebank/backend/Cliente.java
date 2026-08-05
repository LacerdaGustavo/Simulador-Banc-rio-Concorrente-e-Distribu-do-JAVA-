package com.insidebank.backend;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 4444);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== ATM Conectado ===");
            boolean executando = true;

            while (executando) {
                System.out.print("> ");
                String comando = scanner.nextLine();
                if (comando.trim().isEmpty()) continue;

                out.writeUTF(comando);
                String resposta = in.readUTF();
                System.out.println("[Servidor]: " + resposta);

                if (comando.toUpperCase().startsWith("LOGOUT")) {
                    executando = false;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
    }
}