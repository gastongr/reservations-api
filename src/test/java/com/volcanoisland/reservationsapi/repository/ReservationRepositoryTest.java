package com.volcanoisland.reservationsapi.repository;

import com.volcanoisland.reservationsapi.model.Reservation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void testFindInPeriod_noOverlapInLimits() {
        // given
        Reservation john = new Reservation("john@lennon.com", "John Lennon",
                LocalDate.parse("2020-01-10"), LocalDate.parse("2020-01-13"));
        entityManager.persistAndFlush(john);

        // when
        List<Reservation> reservations = reservationRepository.findInPeriod(LocalDate.parse("2020-01-08"), LocalDate.parse("2020-01-10"));
        // then
        Assert.assertTrue(reservations.isEmpty());

        // when
        reservations = reservationRepository.findInPeriod(LocalDate.parse("2020-01-13"), LocalDate.parse("2020-01-16"));
        // then
        Assert.assertTrue(reservations.isEmpty());
    }

    @Test
    public void testFindInPeriod_overlapping() {
        // given
        Reservation paul = new Reservation("paul@mccartney.com", "Paul McCartney",
                LocalDate.parse("2020-02-10"), LocalDate.parse("2020-02-13"));
        entityManager.persistAndFlush(paul);

        // when range contains existing reservation
        List<Reservation> reservations = reservationRepository.findInPeriod(LocalDate.parse("2020-02-01"), LocalDate.parse("2020-02-20"));
        // then
        Assert.assertEquals(1, reservations.size());

        // when range is contained by existing reservation
        reservations = reservationRepository.findInPeriod(LocalDate.parse("2020-02-11"), LocalDate.parse("2020-02-12"));
        // then
        Assert.assertEquals(1, reservations.size());
    }

}