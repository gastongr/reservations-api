package com.volcanoisland.reservationsapi.controller;

import com.volcanoisland.reservationsapi.TestUtils;
import com.volcanoisland.reservationsapi.controller.request.CreateReservationRequest;
import com.volcanoisland.reservationsapi.controller.request.UpdateReservationRequest;
import com.volcanoisland.reservationsapi.model.Reservation;
import com.volcanoisland.reservationsapi.repository.ReservationRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("mysql")
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationControllerConcurrencyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void testMultipleUsersReservingSamePeriod() throws InterruptedException {
        // when multiple concurrent requests attempt to reserve the same days
        TestUtils.runMultithreaded(
                new Runnable() {
                    public void run() {
                        try {
                            createReservation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                10
        );

        // then only 1 was created.
        List<Reservation> createdReservations = reservationRepository.findAllByGuestEmail("robert@plant.com");
        Assert.assertEquals(1, createdReservations.size());
    }

    @Test
    public void testMultipleUsersUpdatingToSamePeriod() throws InterruptedException {
        // given two existing reservations
        final Reservation reservationOne = reservationRepository.save(new Reservation("jimmy@page.com",
                "Jimmy Page", LocalDate.now().plusDays(10), LocalDate.now().plusDays(13)));
        final Reservation reservationTwo = reservationRepository.save(new Reservation("johnpaul@jones.com",
                "John Paul Jones", LocalDate.now().plusDays(20), LocalDate.now().plusDays(23)));

        LocalDate highDemandArrivalDate = LocalDate.now().plusDays(5);
        LocalDate highDemandDepartureDate = LocalDate.now().plusDays(8);

        // when multiple concurrent requests attempt to update a reservation with the same dates
        TestUtils.runAllParallel(
                new Runnable() {
                    public void run() {
                        try {
                            updateReservation(reservationOne.getId(), highDemandArrivalDate, highDemandDepartureDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Runnable() {
                    public void run() {
                        try {
                            updateReservation(reservationTwo.getId(), highDemandArrivalDate, highDemandDepartureDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // then only 1 is updated.
        List<Reservation> updatedReservations = reservationRepository.findInPeriod(highDemandArrivalDate, highDemandDepartureDate);
        Assert.assertEquals(1, updatedReservations.size());
    }


    private void createReservation() {
        restTemplate.postForEntity("/reservations", new CreateReservationRequest("robert@plant.com",
                "Robert Plant", LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)), String.class);
    }

    private void updateReservation(Long reservationId, LocalDate newArrivalDate, LocalDate newDepartureDate) {
        restTemplate.patchForObject("/reservations/"+reservationId,
                new UpdateReservationRequest(newArrivalDate, newDepartureDate), String.class);

    }
}