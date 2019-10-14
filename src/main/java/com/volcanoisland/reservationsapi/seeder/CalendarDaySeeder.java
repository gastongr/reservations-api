package com.volcanoisland.reservationsapi.seeder;

import com.volcanoisland.reservationsapi.model.CalendarDay;
import com.volcanoisland.reservationsapi.repository.CalendarDayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CalendarDaySeeder {

    @Autowired
    private CalendarDayRepository calendarDayRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarDaySeeder.class);

    /**
     * Initializes the CalendarDay table with future dates so that
     * it can be used to build availability data.
     * @param event
     */
    @EventListener(ApplicationReadyEvent.class)
    public void seedCalendarTable(ApplicationReadyEvent event) {
        if (this.calendarDayRepository.count() > 0) {
            return;
        }

        LOGGER.info("Seeding calendar table with future dates");

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(5);

        List<CalendarDay> calendarDays = startDate.datesUntil(endDate).map(d -> new CalendarDay(d))
                .collect(Collectors.toList());

        this.calendarDayRepository.saveAll(calendarDays);
        LOGGER.info("Seeding finished. Added {} days starting from today", calendarDays.size());
    }
}