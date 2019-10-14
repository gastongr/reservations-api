package com.volcanoisland.reservationsapi.dto;

import java.time.LocalDate;

public class PeriodDto {

    private LocalDate from;
    private LocalDate to;

    public PeriodDto(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}
