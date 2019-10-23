package com.volcanoisland.reservationsapi.repository;

import com.volcanoisland.reservationsapi.model.CalendarDay;
import com.volcanoisland.reservationsapi.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ReservationRepository  extends JpaRepository<Reservation, Long> {

    Reservation getReservationByArrivalDateEqualsAndDepartureDateEquals(LocalDate arrivalDate, LocalDate departureDate);

    List<Reservation> findAllByGuestEmail(String email);

    /**
     * Creates a reservation with the provided information given it doesn't overlap with any existing one.
     * @param arrivalDate
     * @param departureDate
     * @param guestEmail
     * @param guestFullName
     * @return int Affected rows
     */
    @Query(value = "INSERT INTO Reservation(arrivalDate, departureDate, guestEmail, guestFullname, status) " +
            "SELECT :arrivalDate," +
            "       :departureDate," +
            "       :guestEmail," +
            "       :guestFullName," +
            "       0 " +
            "FROM Dual " +
            "WHERE NOT EXISTS" +
            "        (SELECT *" +
            "         FROM Reservation R2" +
            "         WHERE :departureDate > R2.arrivalDate" +
            "             AND R2.departureDate > :arrivalDate)", nativeQuery = true)
    @Modifying
    int saveIfAvailable(@Param("arrivalDate") LocalDate arrivalDate,
                                @Param("departureDate") LocalDate departureDate,
                                @Param("guestEmail") String guestEmail,
                                @Param("guestFullName") String guestFullName);

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


