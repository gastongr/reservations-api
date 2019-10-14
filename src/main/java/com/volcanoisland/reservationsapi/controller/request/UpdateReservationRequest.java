package com.volcanoisland.reservationsapi.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Future;
import java.time.LocalDate;

public class UpdateReservationRequest {

    private Long id;
    private String guestEmail;
    private String guestFullName;
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    public UpdateReservationRequest() {
    }

    public UpdateReservationRequest(String guestEmail, String guestFullName, @Future LocalDate arrivalDate, @Future LocalDate departureDate) {
        this.guestEmail = guestEmail;
        this.guestFullName = guestFullName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestFullName() {
        return guestFullName;
    }

    public void setGuestFullName(String guestFullName) {
        this.guestFullName = guestFullName;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

}
