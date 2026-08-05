package com.insidebank.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void inicializarBanco() {

        try (
            Connection conexao = Conexao.conectar();
            Statement stmt = conexao.createStatement()
        ) {

            String sql = """
                CREATE TABLE IF NOT EXISTS contas (
                    id INTEGER PRIMARY KEY,
                    nome TEXT NOT NULL,
                    senha TEXT NOT NULL,
                    saldo REAL NOT NULL
                );
            """;

            stmt.execute(sql);

            System.out.println("Banco inicializado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}