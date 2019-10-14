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
import org.springframework.beans.BeanUtils;
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

    public List<Reservation> findAll() {
        LOGGER.info("Fetching all reservations");
        return this.reservationRepository.findAll();
    }

    public Reservation findOne(final Long id) {
        LOGGER.info("Fetching reservation by Id {}", id);
        return this.reservationRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Reservation create(final CreateReservationRequest request) {
        LOGGER.info("Creating new reservation");
        if (!this.isAvailable(request.getArrivalDate(), request.getDepartureDate())) {
            throw new UnavailableDatesException();
        }

        return this.reservationRepository.save(
                new Reservation(request.getGuestEmail(), request.getGuestFullName(),
                        request.getArrivalDate(), request.getDepartureDate())
        );
    }

    @Transactional
    public Reservation update(final UpdateReservationRequest updateReservationRequest) {
        LOGGER.info("Updating reservation by Id {}", updateReservationRequest.getId());
        Reservation existingEntry = this.reservationRepository.findById(updateReservationRequest.getId())
                .orElseThrow(NotFoundException::new);

        if (existingEntry.getStatus() == ReservationStatusEnum.CANCELLED.getId()) {
            throw new MethodNotAllowedException("Cancelled reservations can not be updated");
        }

        BeanUtils.copyProperties(updateReservationRequest, existingEntry);
        return this.reservationRepository.save(existingEntry);
    }

    @Transactional
    public Reservation cancel(final Long id) {
        LOGGER.info("Cancelling reservation by Id {}", id);
        Reservation existingEntry = this.reservationRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        existingEntry.setStatus(ReservationStatusEnum.CANCELLED.getId());
        return this.reservationRepository.save(existingEntry);
    }

    public boolean isAvailable(LocalDate periodStart, LocalDate periodEnd) {
        List<Reservation> overlappingReservations = this.reservationRepository.findInPeriod(periodStart, periodEnd);
        return overlappingReservations.size() == 0;
    }

    public List<LocalDate> findAvailableDays(LocalDate periodStart, LocalDate periodEnd) {
        if (periodStart.isAfter(periodEnd)) {
            throw new BadRequestException("Invalid dates range supplied");
        }
        return this.reservationRepository.findAvailableDays(periodStart, periodEnd)
                .stream().map(d -> d.getDay())
                .collect(Collectors.toList());
    }

}
