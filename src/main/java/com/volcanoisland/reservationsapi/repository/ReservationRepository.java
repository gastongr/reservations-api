package com.volcanoisland.reservationsapi.repository;

import com.volcanoisland.reservationsapi.model.CalendarDay;
import com.volcanoisland.reservationsapi.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ReservationRepository  extends JpaRepository<Reservation, Long> {

    Reservation findByid(Long productName);

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
     * Find days not having an active reservation on the given date range.
     * @param periodStart
     * @param periodEnd
     * @return The available days
     */
    @Query("SELECT c FROM CalendarDay c " +
            "LEFT JOIN Reservation r ON c.day >= r.arrivalDate " +
            "                      AND c.day < r.departureDate " +
            "WHERE c.day <= :periodEnd AND c.day >= :periodStart " +
            "AND (r.id IS NULL OR r.status = 1) " +
            "ORDER BY c.day ASC")
    List<CalendarDay> findAvailableDays(@Param("periodStart") LocalDate periodStart,
                                        @Param("periodEnd") LocalDate periodEnd);


}


