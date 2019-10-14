package com.volcanoisland.reservationsapi.repository;

import com.volcanoisland.reservationsapi.model.CalendarDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long> {
}
