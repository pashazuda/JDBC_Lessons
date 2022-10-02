package com.jdbc.starter.dao;

import com.jdbc.starter.entity.Flight;
import com.jdbc.starter.entity.TicketEntity;
import com.jdbc.starter.exception.DAOException;
import com.jdbc.starter.utill.ConnectionManagerPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlightDAO implements DAO<Long, Flight> {
    private static final FlightDAO INSTANCE = new FlightDAO();

    private static final String FIND_ALL_SQL = """
    SELECT  f.id,
            f.status,
            f.aircraft_id,
            f.arrival_airport_code,
            f.arrival_date,
            f.departure_airport_code,
            f.departure_date,
            f.flight_no
            FROM flight f
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private FlightDAO() {
    }

    public static FlightDAO getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Flight save(Flight ticket) {
        return null;
    }

    @Override
    public void update(Flight ticket) {

    }
    @Override
    public Optional<Flight> findById(Long id) {
        try (var connection = ConnectionManagerPool.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public Optional<Flight> findById(Long id, Connection connection) {
        try (var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setLong(1, id);
            var resultSet = prepareStatement.executeQuery();
            Flight flight = null;
            if (resultSet.next()) {
                flight = buildFlightEntity(resultSet);
            }
            return Optional.ofNullable(flight);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }


    private Flight buildFlightEntity(ResultSet resultSet) throws SQLException {
        return new Flight(
                resultSet.getLong("id"),
                resultSet.getString("flight_no"),
                resultSet.getTimestamp("departure_date").toLocalDateTime(),
                resultSet.getString("departure_airport_code"),
                resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                resultSet.getString("arrival_airport_code"),
                resultSet.getInt("aircraft_id"),
                resultSet.getString("status")
        );
    }

    @Override
    public List<Flight> findAll() {
        return null;
    }
}
