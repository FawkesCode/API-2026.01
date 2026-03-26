package com.fawkes.api.Repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import io.github.cdimascio.dotenv.Dotenv;

@Repository
public class ConnectionRepository {
    public Connection getConnection(){

        Dotenv dotenv = Dotenv.load();
        String senhaBd =  dotenv.get("SENHA_BANCO_DE_DADOS");

        try {
            return DriverManager.getConnection("jdbc:mysql://localhost/transacoes_bd","root",senhaBd);
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados", e);
        }
    }
}
