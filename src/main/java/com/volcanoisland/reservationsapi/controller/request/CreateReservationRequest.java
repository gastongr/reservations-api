package com.volcanoisland.reservationsapi.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.volcanoisland.reservationsapi.validation.ValidReservationPeriod;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@ValidReservationPeriod(
        arrivalField = "arrivalDate",
        departureField = "departureDate"
)
public class CreateReservationRequest {

    @Email
    @NotBlank
    private String guestEmail;

    @NotBlank
    private String guestFullName;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    public CreateReservationRequest() {
    }

    public CreateReservationRequest(@Email @NotBlank String guestEmail, @NotBlank String guestFullName, @NotNull @Future LocalDate arrivalDate, @NotNull @Future LocalDate departureDate) {
        this.guestEmail = guestEmail;
        this.guestFullName = guestFullName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
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
