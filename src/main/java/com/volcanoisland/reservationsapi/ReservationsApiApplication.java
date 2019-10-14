package com.volcanoisland.reservationsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.volcanoisland.reservationsapi")
public class ReservationsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationsApiApplication.class, args);
	}

}
