package com.jdbc.starter;

import com.jdbc.starter.dao.TicketDAO;
import com.jdbc.starter.dto.TicketFilter;
import com.jdbc.starter.entity.TicketEntity;

import java.math.BigDecimal;
import java.util.Optional;

public class DAORunner {
    public static void main(String[] args) {
//        saveTest();
//        deleteTest();
//        updateTest();
//        filterTest();
        var ticket = TicketDAO.getInstance().findById(5L);
        System.out.println(ticket);
    }

    private static void filterTest() {
        var ticketFilter = new TicketFilter(10, 2, null, "A1");
        var tickets = TicketDAO.getInstance().findAll(ticketFilter);
        System.out.println();
    }

    private static void updateTest() {
        var ticketDAO = TicketDAO.getInstance();
        var maybeticket = ticketDAO.findById(2L);
        maybeticket.ifPresent(ticket -> {
            ticket.setCost(BigDecimal.valueOf(228));
            ticketDAO.update(ticket);
        });
    }

    private static void deleteTest() {
        var ticketDAO = TicketDAO.getInstance();
        var deleteResult = ticketDAO.delete(56L);
        System.out.println(deleteResult);
    }

    private static void saveTest() {
        var ticketDAO = TicketDAO.getInstance();
        var ticketEntity = new TicketEntity();
        ticketEntity.setPassengerNo("123214");
        ticketEntity.setPassengerName("Test1");
//        ticketEntity.setFlight(3L);
        ticketEntity.setSeatNo("B3");
        ticketEntity.setCost(BigDecimal.TEN);
        var savedTicket = ticketDAO.save(ticketEntity);
        System.out.println(savedTicket);
    }
}
