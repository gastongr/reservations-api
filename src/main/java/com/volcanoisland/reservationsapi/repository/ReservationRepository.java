package com.volcanoisland.reservationsapi.repository;

import com.volcanoisland.reservationsapi.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ReservationRepository  extends JpaRepository<Reservation, Long> {

    Reservation findByid(Long id);

    List<Reservation> findAllByGuestEmail(String email);

    /**
     * Find active reservations that overlap with the given date range.
     * @param periodStart
     * @param periodEnd
     * @return The overlapping active reservations
     */
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status = 0 AND :periodEnd > r.arrivalDate AND r.departureDate > :periodStart")
    List<Reservation> findInPeriod(@Param("periodStart") LocalDate periodStart,
                                   @Param("periodEnd") LocalDate periodEnd);


    /**
     * Find active reservations that overlap with the given date range excluding that of the provided Id.
     * @param periodStart
     * @param periodEnd
     * @param excludeId
     * @return The overlapping active reservations
     */
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status = 0 AND :periodEnd > r.arrivalDate AND r.departureDate > :periodStart AND r.id <> :excludeId")
    List<Reservation> findInPeriodExcluding(@Param("periodStart") LocalDate periodStart,
                                          @Param("periodEnd") LocalDate periodEnd,
                                          @Param("excludeId") Long excludeId);

}


