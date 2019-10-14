package com.volcanoisland.reservationsapi.controller;

import com.volcanoisland.reservationsapi.controller.request.CreateReservationRequest;
import com.volcanoisland.reservationsapi.dto.ReservationDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class ReservationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateAndGetReservation() {
        // when creating resource
        ResponseEntity<ReservationDto> response = restTemplate.postForEntity("/reservations",
                new CreateReservationRequest("ringo@starr.com", "Ringo Starr", LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)),
                ReservationDto.class);

        // then receive http status code 201
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertNotNull(response.getBody().getId());
        Long id = response.getBody().getId();

        // when requesting resource
        ResponseEntity<ReservationDto> reservation = restTemplate.getForEntity("/reservations/"+id, ReservationDto.class);

        // then
        Assert.assertEquals(HttpStatus.OK, reservation.getStatusCode());
        Assert.assertEquals("ringo@starr.com", reservation.getBody().getGuestEmail());
        Assert.assertEquals("Ringo Starr", reservation.getBody().getGuestFullName());
        Assert.assertEquals(LocalDate.now().plusDays(1), reservation.getBody().getArrivalDate());
        Assert.assertEquals(LocalDate.now().plusDays(4), reservation.getBody().getDepartureDate());
    }

    @Test
    public void testGetReservations() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/reservations", String.class);

        // then
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

}