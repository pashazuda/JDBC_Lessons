package com.jdbc.starter;

import com.jdbc.starter.utill.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;

public class BlobRunner {
    public static void main(String[] args) throws SQLException, IOException {
//        blob - binary large object - картинки видел аудио в постгре bytea
//        clob - символьный large object - в постгре TEXT
//        saveImage();
          getImage();
    }

    private static void getImage() throws SQLException, IOException {
        var sql = """
                SELECT image
                FROM aircraft
                WHERE id = ?
                """;
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setInt(1, 1);
            var resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                var image = resultSet.getBytes(1);
                Files.write(Path.of("image.jpg"), image, StandardOpenOption.CREATE);
            }
        }
    }

    private static void saveImage() throws SQLException, IOException {
//        var sql = """
//                UPDATE aircraft
//                SET image =?
//                WHERE id =1
//                """;
//        try (var connection = ConnectionManager.open();
//             var prepareStatement = connection.prepareStatement(sql)) {
//            connection.setAutoCommit(false);
//            // так как блоб и клоб большие объекты, следует открывать и закрывать транзакцию, в случае постгреса не надо
//            var blob = connection.createBlob();
//            blob.setBytes(1, Files.readAllBytes(Path.of("resources", "boing.png")));
//            prepareStatement.setBlob(1, blob);
//            prepareStatement.executeUpdate();
//            connection.commit();
        var sql = """
                UPDATE aircraft
                SET image =?
                WHERE id =1
                """;
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setBytes(1, Files.readAllBytes(Path.of("D:\\Kata\\ПП\\JDBC_Lessons\\JDBC_Project\\resources\\boing.jpg")));
            prepareStatement.executeUpdate();
        }
    }
}
