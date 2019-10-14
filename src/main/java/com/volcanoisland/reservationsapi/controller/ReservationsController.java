package com.volcanoisland.reservationsapi.controller;

import com.volcanoisland.reservationsapi.controller.request.CreateReservationRequest;
import com.volcanoisland.reservationsapi.controller.request.UpdateReservationRequest;
import com.volcanoisland.reservationsapi.dto.ReservationDto;
import com.volcanoisland.reservationsapi.dto.ReservationDtoBuilder;
import com.volcanoisland.reservationsapi.model.Reservation;
import com.volcanoisland.reservationsapi.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationsController {

    @Autowired
    private ReservationService reservationService;

    /**
     * Fetch all reservations.
     * @return ResponseEntity<List<ReservationDto>>
     */
    @GetMapping()
    public ResponseEntity<List<ReservationDto>> getReservations() {
        List<Reservation> reservations = reservationService.findAll();

        return ResponseEntity.ok().body(
                reservations.stream().map(k -> ReservationDtoBuilder.aReservationDto().fromReservation(k).build())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Fetch a single reservation by it's ID.
     * @param id
     * @return ResponseEntity<ReservationDto>
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable("id") Long id) {
        Reservation reservation = reservationService.findOne(id);

        return ResponseEntity.ok().body(ReservationDtoBuilder.aReservationDto().fromReservation(reservation).build());
    }

    /**
     * Partially update an existing reservation by ID.
     * @param request
     * @param id
     * @return ResponseEntity<ReservationDto>
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ReservationDto> updateReservation(@RequestBody @Valid UpdateReservationRequest request, @PathVariable("id") Long id) {
        request.setId(id);
        Reservation updatedReservation = reservationService.update(request);

        return ResponseEntity.ok().body(ReservationDtoBuilder.aReservationDto()
                .fromReservation(updatedReservation)
                .build());
    }

    /**
     * Cancel a reservation by ID.
     * @param id
     * @return ResponseEntity<ReservationDto>
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable("id") Long id) {
        Reservation updatedReservation = reservationService.cancel(id);

        return ResponseEntity.ok().body(ReservationDtoBuilder.aReservationDto()
                .fromReservation(updatedReservation)
                .build());
    }

    /**
     * Create a new reservation with the provided details.
     * @param request
     * @return ResponseEntity<ReservationDto>
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ReservationDto> createReservation(@RequestBody @Valid CreateReservationRequest request) {
        Reservation newReservation = reservationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ReservationDtoBuilder.aReservationDto()
                .withId(newReservation.getId()).build());
    }

}

