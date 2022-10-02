package com.jdbc.starter.dto;

// Объект с пречнем полей
public record TicketFilter(int limit,
                           int offset,
                           String passengerName,
                           String seatNo) {

}
