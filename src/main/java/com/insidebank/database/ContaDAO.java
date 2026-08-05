package com.insidebank.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.insidebank.backend.Conta;

public class ContaDAO {

    public List<Conta> listarTodas() {

        //Cria uma lista vazia onde serão colocadas todas as contas encontradas no banco.
        List<Conta> contas = new ArrayList<>();

        String sql = "SELECT * FROM contas";

        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                //Executa o SELECT
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                //Para cada linha cria um objeto conta//
                Conta conta = new Conta( 
                        rs.getInt("id"),
                        rs.getDouble("saldo"),
                        rs.getString("senha"),
                        rs.getString("nome"),
                        rs.getInt("banco_id")
                );

                contas.add(conta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contas;
    }



    public void inserir(Conta conta) {

        String sql = """
            INSERT INTO contas(id, nome, senha, saldo, banco_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, conta.getId());
            stmt.setString(2, conta.getNome());
            stmt.setString(3, conta.getSenha());
            stmt.setDouble(4, conta.getSaldo());
            stmt.setInt(5, conta.getBancoId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public void atualizar(Conta conta) {

        String sql = """
            UPDATE contas
            SET nome = ?, senha = ?, saldo = ?, banco_id = ?
            WHERE id = ?
        """;

        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, conta.getNome());
            stmt.setString(2, conta.getSenha());
            stmt.setDouble(3, conta.getSaldo());
            stmt.setInt(4, conta.getBancoId());
            stmt.setInt(5, conta.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}