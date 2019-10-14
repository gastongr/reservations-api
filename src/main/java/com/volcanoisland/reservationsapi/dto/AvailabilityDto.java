package com.volcanoisland.reservationsapi.dto;

import java.time.LocalDate;
import java.util.List;

public class AvailabilityDto {

    private PeriodDto period;
    private List<LocalDate> availableDates;

    public AvailabilityDto(PeriodDto period, List<LocalDate> availableDates) {
        this.period = period;
        this.availableDates = availableDates;
    }

    public PeriodDto getPeriod() {
        return period;
    }

    public void setPeriod(PeriodDto period) {
        this.period = period;
    }

    public List<LocalDate> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(List<LocalDate> availableDates) {
        this.availableDates = availableDates;
    }
}
