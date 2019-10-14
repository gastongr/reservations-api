package com.volcanoisland.reservationsapi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ReservationPeriodValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReservationPeriod {
    String message() default "Invalid reservation period";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String arrivalField();

    String departureField();
}