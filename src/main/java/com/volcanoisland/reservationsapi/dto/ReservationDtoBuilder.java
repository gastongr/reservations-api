package com.volcanoisland.reservationsapi.dto;

import com.volcanoisland.reservationsapi.model.Reservation;
import com.volcanoisland.reservationsapi.model.ReservationStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ReservationDtoBuilder {
    private Long id;
    private String guestEmail;
    private String guestFullName;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private ReservationStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ReservationDtoBuilder() {
    }

    public static ReservationDtoBuilder aReservationDto() {
        return new ReservationDtoBuilder();
    }

    public ReservationDtoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ReservationDtoBuilder withGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
        return this;
    }

    public ReservationDtoBuilder withGuestFullName(String guestFullName) {
        this.guestFullName = guestFullName;
        return this;
    }

    public ReservationDtoBuilder withArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
        return this;
    }

    public ReservationDtoBuilder withDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public ReservationDtoBuilder withStatus(ReservationStatusEnum status) {
        this.status = status;
        return this;
    }

    public ReservationDtoBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ReservationDtoBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public ReservationDtoBuilder fromReservation(Reservation reservation) {
        this.id = reservation.getId();
        this.guestEmail = reservation.getGuestEmail();
        this.guestFullName = reservation.getGuestFullName();
        this.arrivalDate = reservation.getArrivalDate();
        this.departureDate = reservation.getDepartureDate();
        this.status = ReservationStatusEnum.getById(reservation.getStatus());
        this.updatedAt = reservation.getUpdatedAt();
        this.createdAt = reservation.getCreatedAt();
        return this;
    }

    public ReservationDto build() {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(id);
        reservationDto.setGuestEmail(guestEmail);
        reservationDto.setGuestFullName(guestFullName);
        reservationDto.setArrivalDate(arrivalDate);
        reservationDto.setDepartureDate(departureDate);
        reservationDto.setStatus(status);
        reservationDto.setCreatedAt(createdAt);
        reservationDto.setUpdatedAt(updatedAt);
        return reservationDto;
    }
}
