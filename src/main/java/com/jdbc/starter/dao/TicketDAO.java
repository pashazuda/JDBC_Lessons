package com.jdbc.starter.dao;

import com.jdbc.starter.dto.TicketFilter;
import com.jdbc.starter.entity.Flight;
import com.jdbc.starter.entity.TicketEntity;
import com.jdbc.starter.exception.DAOException;
import com.jdbc.starter.utill.ConnectionManagerPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class TicketDAO implements DAO<Long, TicketEntity>{
    private static final TicketDAO INSTANCE = new TicketDAO();
    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id =?
            """;
    public static final String SAVE_SQL = """
    INSERT INTO ticket (passenger_no, passenger_name, flight_id, seat_no, cost)
            VALUES (?,?,?,?,?)
            """;

    private static final String UPDATE_SQL = """
    UPDATE ticket
            SET passenger_no =?,
            passenger_name =?,
            flight_id =?,
            seat_no =?,
            cost =?
            WHERE id =?
            """;
    private static final String FIND_ALL_SQL = """
    SELECT ticket.id,
            passenger_no,
            passenger_name,
            flight_id,
            seat_no,
            cost,
            f.status,
            f.aircraft_id,
            f.arrival_airport_code,
            f.arrival_date,
            f.departure_airport_code,
            f.departure_date,
            f.flight_no
            FROM ticket
            JOIN flight f
                on ticket.flight_id = f.id
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE ticket.id =?
            """;
    private final FlightDAO flightDAO = FlightDAO.getInstance();

    private TicketDAO() {}

    public List<TicketEntity> findAll(TicketFilter filter) {
        List<Object> parameters = new ArrayList<Object>();
        List<String> whereSql = new ArrayList<>();
        if (filter.seatNo() != null) {
            whereSql.add(" seat_no LIKE ? ");
            parameters.add("%" + filter.seatNo() + "%");
        }
        if (filter.passengerName() != null) {
            whereSql.add(" passangerName = ? ");
            parameters.add(filter.passengerName());
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        var where = whereSql.stream().collect(joining(" AND ", " WHERE ", " LIMIT ? OFFSET ? "));
        var sql = FIND_ALL_SQL + where;
        try (var connection = ConnectionManagerPool.get();
             var prepareStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                prepareStatement.setObject(i + 1, parameters.get(i));
            }
            System.out.println(prepareStatement);
            var resultSet = prepareStatement.executeQuery();
            List<TicketEntity> ticketEntities = new ArrayList<>();
            while (resultSet.next()) {
                ticketEntities.add(buildTicketEntity(resultSet));
            }
            return ticketEntities;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<TicketEntity> findAll() {
        try (var connection = ConnectionManagerPool.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            List<TicketEntity> ticketEntities = new ArrayList<>();
            while (resultSet.next()) {
                ticketEntities.add(buildTicketEntity(resultSet));
            }
            return ticketEntities;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public Optional<TicketEntity> findById(Long id) {
        try (var connection = ConnectionManagerPool.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setLong(1, id);
            var resultSet = prepareStatement.executeQuery();
            TicketEntity ticket = null;
            if (resultSet.next()) {
                ticket = buildTicketEntity(resultSet);
            }
            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private TicketEntity buildTicketEntity(ResultSet resultSet) throws SQLException {
        TicketEntity ticket;
        ticket = new TicketEntity(
                resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                flightDAO.findById(resultSet.getLong("flight_id"),
                        resultSet.getStatement().getConnection()).orElse(null),
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
        return ticket;
    }

    public void update(TicketEntity ticket) {
        try (var connection = ConnectionManagerPool.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setString(1, ticket.getPassengerNo());
            prepareStatement.setString(2, ticket.getPassengerName());
            prepareStatement.setLong(3, ticket.getFlight().id());
            prepareStatement.setString(4, ticket.getSeatNo());
            prepareStatement.setBigDecimal(5, ticket.getCost());
            prepareStatement.setLong(6, ticket.getId());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public TicketEntity save(TicketEntity ticket) {
        try (var connection = ConnectionManagerPool.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, ticket.getPassengerNo());
            prepareStatement.setString(2, ticket.getPassengerName());
            prepareStatement.setLong(3, ticket.getFlight().id());
            prepareStatement.setString(4, ticket.getSeatNo());
            prepareStatement.setBigDecimal(5, ticket.getCost());
            prepareStatement.executeUpdate();
            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                ticket.setId(generatedKeys.getLong("id"));
            }
            return ticket;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public boolean delete(Long id) {
        try (var connection = ConnectionManagerPool.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setLong(1, id);
            return prepareStatement.executeUpdate() > 0; // так как возвращает число удаленных строк
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public static TicketDAO getInstance() {
        return INSTANCE;
    }

}
