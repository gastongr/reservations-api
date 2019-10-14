package com.volcanoisland.reservationsapi.controller;

import com.volcanoisland.reservationsapi.dto.AvailabilityDto;
import com.volcanoisland.reservationsapi.dto.PeriodDto;
import com.volcanoisland.reservationsapi.service.ReservationService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Future;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/availability", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class AvailabilityController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping()
    public ResponseEntity<AvailabilityDto> getAvailability(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Future LocalDate from,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Future LocalDate to) {
        from = ObjectUtils.defaultIfNull(from, LocalDate.now().plusDays(1));
        to = ObjectUtils.defaultIfNull(to, from.plusMonths(1));

        List<LocalDate> availableDays = reservationService.findAvailableDays(from, to);

        return ResponseEntity.ok().body(new AvailabilityDto(new PeriodDto(from, to), availableDays));
    }

}

