package com.volcanoisland.reservationsapi.service;

import com.volcanoisland.reservationsapi.controller.request.CreateReservationRequest;
import com.volcanoisland.reservationsapi.controller.request.UpdateReservationRequest;
import com.volcanoisland.reservationsapi.exception.BadRequestException;
import com.volcanoisland.reservationsapi.exception.MethodNotAllowedException;
import com.volcanoisland.reservationsapi.exception.NotFoundException;
import com.volcanoisland.reservationsapi.exception.UnavailableDatesException;
import com.volcanoisland.reservationsapi.model.Reservation;
import com.volcanoisland.reservationsapi.model.ReservationStatusEnum;
import com.volcanoisland.reservationsapi.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    /**
     * Fetch all reservations on the database.
     * @return List<Reservation>
     */
    public List<Reservation> findAll() {
        LOGGER.info("Fetching all reservations");
        return this.reservationRepository.findAll();
    }

    /**
     * Fetches a single reservation by Id or fails.
     * @param id
     * @return Reservation
     */
    public Reservation findOne(final Long id) {
        LOGGER.info("Fetching reservation by Id {}", id);
        return this.reservationRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    /**
     * Creates a new Reservation entry in the database, given there are no overlaps, using a conditional insert.
     *
     * @param request
     * @return Reservation
     */
    @Transactional
    public Reservation create(final CreateReservationRequest request) {
        LOGGER.info("Creating new reservation {}", request);

        int affectedRows = this.reservationRepository.saveIfAvailable(request.getArrivalDate(),
                request.getDepartureDate(), request.getGuestEmail(), request.getGuestFullName());

        if (affectedRows == 0) {
            throw new UnavailableDatesException();
        }

        return this.reservationRepository.getReservationByArrivalDateEqualsAndDepartureDateEquals(request.getArrivalDate(),
                request.getDepartureDate());
    }

    /**
     * Updates an existing Reservation on the database.
     *
     * Concurrency considerations:
     * Since a different thread could save an overlapping reservation between the availability check and the actual
     * update on this method, we need to avoid concurrent execution thus the synchronized keyword.
     *
     * If this application had multiple instances accessing to the same database, then thread synchronization would
     * not be enough. In such case database locking or preferable database constrains would have to be used
     * to avoid possible overlapping dates.
     *
     * @param request
     * @return Reservation
     */
    public synchronized Reservation update(final UpdateReservationRequest request) {
        LOGGER.info("Updating reservation {}", request);
        Reservation existingEntry = this.reservationRepository.findById(request.getId())
                .orElseThrow(NotFoundException::new);

        if (ReservationStatusEnum.CANCELLED.getId().equals(existingEntry.getStatus())) {
            throw new MethodNotAllowedException("Cancelled reservations can not be updated");
        }
        if (!this.isAvailablePeriodExcluding(request.getArrivalDate(), request.getDepartureDate(), request.getId())) {
            throw new UnavailableDatesException();
        }

        // Copy non-null properties to entity:
        if (request.getGuestEmail() != null) {
            existingEntry.setGuestEmail(request.getGuestEmail());
        }
        if (request.getGuestFullName() != null) {
            existingEntry.setGuestFullName(request.getGuestFullName());
        }
        if (request.getArrivalDate() != null) {
            existingEntry.setArrivalDate(request.getArrivalDate());
        }
        if (request.getDepartureDate() != null) {
            existingEntry.setDepartureDate(request.getDepartureDate());
        }

        return this.reservationRepository.save(existingEntry);
    }

    /**
     * Cancels an existing Reservation by Id, updating the status field on the database.
     * @param id
     * @return Reservation
     */
    public Reservation cancel(final Long id) {
        LOGGER.info("Cancelling reservation by Id {}", id);
        Reservation existingEntry = this.reservationRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        existingEntry.setStatus(ReservationStatusEnum.CANCELLED.getId());
        return this.reservationRepository.save(existingEntry);
    }

    /**
     * Return whether a given period is available for reservation or not.
     * In order to be available no active reservations have to overlap with the given dates.
     * @param periodStart
     * @param periodEnd
     * @return boolean
     */
    public boolean isAvailablePeriod(LocalDate periodStart, LocalDate periodEnd) {
        List<Reservation> overlappingReservations = this.reservationRepository.findInPeriod(periodStart, periodEnd);
        return overlappingReservations.size() == 0;
    }

    /**
     * Return whether a given period is available for reservation or not.
     * In order to be available no active reservations have to overlap with the given dates.
     * This method also ignores a reservation with the id provided.
     * @param periodStart
     * @param periodEnd
     * @param reservationIdToExclude
     * @return boolean
     */
    public boolean isAvailablePeriodExcluding(LocalDate periodStart, LocalDate periodEnd, Long reservationIdToExclude) {
        List<Reservation> overlappingReservations = this.reservationRepository
                .findInPeriodExcluding(periodStart, periodEnd, reservationIdToExclude);
        return overlappingReservations.size() == 0;
    }

    /**
     * Fetches the days from the given range in which the campsite is available for reservation.
     * @param periodStart
     * @param periodEnd
     * @return List<LocalDate>
     */
    public List<LocalDate> findAvailableDays(LocalDate periodStart, LocalDate periodEnd) {
        if (periodStart.isAfter(periodEnd)) {
            throw new BadRequestException("Invalid dates range supplied");
        }
        LOGGER.info("Looking up availability between dates {} {}", periodStart, periodEnd);
        return this.reservationRepository.findAvailableDays(periodStart, periodEnd)
                .stream().map(d -> d.getDay())
                .collect(Collectors.toList());
    }

}
