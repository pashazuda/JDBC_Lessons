package com.jdbc.starter;

import com.jdbc.starter.utill.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcInjection {
    public static void main(String[] args) throws SQLException {
        // SQLInjection это когда в передаваему строку можно передать запрос '2 OR 1 = 1; DROP TABLE IF EXISTS info;'
        String flightId = "2";
        var result = getTicketsByFlightId(flightId);
        System.out.println(result);

    }

    private static List<Long> getTicketsByFlightId(String flightId) throws SQLException {
        String sql = """
                SELECT id
                FROM ticket
                WHERE flight_id = %s
                """.formatted(flightId);
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
//                result.add(resultSet.getLong("id"));
                result.add(resultSet.getObject("id", Long.class)); // так как может содержать NULL
            }
        }
        return result;
    }
}
