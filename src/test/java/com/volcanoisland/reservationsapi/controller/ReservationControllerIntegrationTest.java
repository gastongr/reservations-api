package com.volcanoisland.reservationsapi.controller;

import com.volcanoisland.reservationsapi.controller.request.CreateReservationRequest;
import com.volcanoisland.reservationsapi.controller.request.UpdateReservationRequest;
import com.volcanoisland.reservationsapi.dto.ReservationDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class ReservationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void testCreateUpdateAndGetReservation() {
        // when creating a reservation
        ResponseEntity<ReservationDto> createResponse = restTemplate.postForEntity("/reservations",
                new CreateReservationRequest("ringo@starr.com", "Ringo Starr",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)), ReservationDto.class);

        // then receive http status code 201 and reservation id
        Assert.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assert.assertNotNull(createResponse.getBody().getId());
        Long id = createResponse.getBody().getId();


        // when requesting reservation
        ResponseEntity<ReservationDto> reservation = restTemplate.getForEntity("/reservations/"+id, ReservationDto.class);

        // then receive original data
        Assert.assertEquals(HttpStatus.OK, reservation.getStatusCode());
        Assert.assertEquals("ringo@starr.com", reservation.getBody().getGuestEmail());
        Assert.assertEquals("Ringo Starr", reservation.getBody().getGuestFullName());
        Assert.assertEquals(LocalDate.now().plusDays(1), reservation.getBody().getArrivalDate());
        Assert.assertEquals(LocalDate.now().plusDays(4), reservation.getBody().getDepartureDate());


        // when updating a reservation
        LocalDate newArrivalDate = LocalDate.now().plusDays(2);
        LocalDate newDepartureDate = LocalDate.now().plusDays(5);
        ReservationDto updateResponse = restTemplate.patchForObject("/reservations/"+id,
                new UpdateReservationRequest("ringo@beatles.com", "Ringo S.",
                        newArrivalDate, newDepartureDate), ReservationDto.class);

        // then receive updated data
        Assert.assertEquals("ringo@beatles.com", updateResponse.getGuestEmail());
        Assert.assertEquals("Ringo S.", updateResponse.getGuestFullName());
        Assert.assertEquals(newArrivalDate, updateResponse.getArrivalDate());
        Assert.assertEquals(newDepartureDate, updateResponse.getDepartureDate());

        // when requesting reservation again
        ResponseEntity<ReservationDto> updatedReservation = restTemplate.getForEntity("/reservations/"+id, ReservationDto.class);

        // then receive updated data
        Assert.assertEquals(HttpStatus.OK, reservation.getStatusCode());
        Assert.assertEquals("ringo@beatles.com", updatedReservation.getBody().getGuestEmail());
        Assert.assertEquals("Ringo S.", updatedReservation.getBody().getGuestFullName());
        Assert.assertEquals(newArrivalDate, updatedReservation.getBody().getArrivalDate());
        Assert.assertEquals(newDepartureDate, updatedReservation.getBody().getDepartureDate());

    }

    @Test
    public void testCreateOverlappedReservation() {
        // when creating a reservation
        ResponseEntity<ReservationDto> createResponse = restTemplate.postForEntity("/reservations",
                new CreateReservationRequest("john@bonham.com", "John Bonham",
                        LocalDate.now().plusDays(5), LocalDate.now().plusDays(8)), ReservationDto.class);

        // then receive http status code 201 and reservation id
        Assert.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assert.assertNotNull(createResponse.getBody().getId());

        // when creating an overlapping reservation
        ResponseEntity<String> overlappingCreateResponse = restTemplate.postForEntity("/reservations",
                new CreateReservationRequest("jimmy@page.com", "Jimmy Page",
                        LocalDate.now().plusDays(5), LocalDate.now().plusDays(8)), String.class);

        // then receive http status code 406 Conflict
        Assert.assertEquals(HttpStatus.CONFLICT, overlappingCreateResponse.getStatusCode());
    }

    @Test
    public void testGetReservations() {
        // when fetching all reservations
        ResponseEntity<String> response = restTemplate.getForEntity("/reservations", String.class);
        // then receive http status code 200
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }



}