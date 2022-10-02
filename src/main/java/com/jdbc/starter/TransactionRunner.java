package com.jdbc.starter;

import com.jdbc.starter.utill.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {
    public static void main(String[] args) throws SQLException {
        long flightId = 4;
//       var deleFlightSql = "DELETE FROM flight WHERE id = ?";
//        var deleTicketSql = "DELETE FROM ticket WHERE flight_id = ?";
        var deleFlightSql = "DELETE FROM flight WHERE id = " + flightId;
        var deleTicketSql = "DELETE FROM ticket WHERE flight_id = " + flightId;
        Connection connection = null;
        Statement statement = null;
//        PreparedStatement deleteFlightStatement = null;
//        PreparedStatement deleteTicketStatement = null;
        try {
            connection = ConnectionManager.open();
            connection.setAutoCommit(false); // убираем автоматическое выполнение запросов
            statement = connection.createStatement();
            statement.addBatch(deleTicketSql);
            statement.addBatch(deleFlightSql);

            var ints = statement.executeBatch();
            // только Statement позволяет использовать Batch запросы
//            deleteFlightStatement = connection.prepareStatement(deleFlightSql);
//            deleteTicketStatement = connection.prepareStatement(deleTicketSql);
//            connection.setAutoCommit(false); // убираем автоматическое выполнение запросов
//
//            deleteFlightStatement.setLong(1, flightId);
//            deleteTicketStatement.setLong(1, flightId);
//
//            var deletedTicketResult = deleteTicketStatement.executeUpdate();
//            var deletedFlightResult = deleteFlightStatement.executeUpdate();

            connection.commit(); // фиксированние нашей транзакции (запроса), и удаляться все записи из наших таблиц
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally { // теперь необходимо выполнить закрытие транзакций
            if (connection!= null) {
                connection.close();
            } if (statement != null) {
                statement.close();
            }
//            if (deleteFlightStatement!=null) {
//                deleteFlightStatement.close();
//            }
//            if (deleteTicketStatement!=null) {
//                deleteTicketStatement.close();
//            }
        }
    }
}
