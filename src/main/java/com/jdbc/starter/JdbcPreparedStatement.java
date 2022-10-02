package com.jdbc.starter;

import com.jdbc.starter.utill.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcPreparedStatement {
    public static void main(String[] args) throws SQLException {
        // SQLInjection это когда в передаваему строку можно передать запрос '2 OR 1 = 1; DROP TABLE IF EXISTS info;'
//        Long flightId = 2L;
//        var result = getTicketsByFlightId(flightId);
//        System.out.println(result);
        var result = getFlightsBetween(LocalDate.of(2020, 1, 1).atStartOfDay(), LocalDateTime.now());
        System.out.println(result);
        checkMetaData();
    }

    // метод для проверки метаданных
    private static void checkMetaData() throws SQLException {
        try (var connection = ConnectionManager.open()) {
            var metaData = connection.getMetaData();
            var catalogs = metaData.getCatalogs();
            while (catalogs.next()) {
//                System.out.println(catalog.getString(1));
                var catalog = catalogs.getString(1);
                var schemas = metaData.getSchemas();
                while (schemas.next()) {
//                    System.out.println(schemas.getString("TABLE_SCHEM"));
                    var schema = schemas.getString("TABLE_SCHEM");
                    var tables = metaData.getTables(catalog, schema, "%", new String[] { "TABLE"}); // %-vse kolonki
                    if (schema.equals("public")) {
                        while (tables.next()) {
                            System.out.println(tables.getString("TABLE_NAME"));
//                            metaData.getColumns(1);
                        }
                    }
                }
            }
        }
    }

    private static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = """
                SELECT id
                FROM flight
                WHERE departure_date BETWEEN ? AND ?
                """;
        var result = new ArrayList<Long>();
        try (var connection = ConnectionManager.open();
        var prepareStatement = connection.prepareStatement(sql)) {
            System.out.println(prepareStatement);
            prepareStatement.setFetchSize(5);
            prepareStatement.setQueryTimeout(10); // необходиом устанавливание тайм-аута
            prepareStatement.setMaxRows(100); // аналог лимита
            prepareStatement.setTimestamp(1, Timestamp.valueOf(start));
            System.out.println(prepareStatement);
            prepareStatement.setTimestamp(2, Timestamp.valueOf(end));
            System.out.println(prepareStatement);
            var resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getObject("id", Long.class));

            }
        }
        return result;
    }

    private static List<Long> getTicketsByFlightId(Long flightId) throws SQLException {
        String sql = """
                SELECT id
                FROM ticket
                WHERE flight_id = ?
                """;
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setLong(1, flightId);
            var resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
//                result.add(resultSet.getLong("id"));
                result.add(resultSet.getObject("id", Long.class)); // так как может содержать NULL
            }
        }
        return result;
    }
}
