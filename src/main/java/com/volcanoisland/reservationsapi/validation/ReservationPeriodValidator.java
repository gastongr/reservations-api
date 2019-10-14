package com.volcanoisland.reservationsapi.validation;

import com.volcanoisland.reservationsapi.exception.BadRequestException;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class ReservationPeriodValidator implements ConstraintValidator<ValidReservationPeriod, Object> {

    private String arrivalField;
    private String departureField;
    private static final int MAX_STAY_DAYS = 3;
    private static final int MIN_STAY_DAYS = 1;

    private static final int MAX_ANTICIPATION_MONTHS = 1;
    private static final int MIN_ANTICIPATION_DAYS = 1;

    private static final String ERROR_ARRIVAL_AND_DEPARTURE_REQUIRED = "Both arrivalDate and departureDate should be provided";
    private static final String ERROR_MIN_MAX_STAY = "Reservation length should be between 1 to 3 days";
    private static final String ERROR_MIN_MAX_ANTICIPATION = "Reservations should be placed with a maximum anticipation of 1 month and minimum of 1 day before arrival";
    private static final String ERROR_INVALID_DATE_RANGE = "Invalid dates range supplied";

    public void initialize(ValidReservationPeriod constraintAnnotation) {
        this.arrivalField = constraintAnnotation.arrivalField();
        this.departureField = constraintAnnotation.departureField();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {

        final LocalDate arrivalDate = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(arrivalField);
        final LocalDate departureDate = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(departureField);

        // Either both dates are null or none should be
        if ((arrivalDate == null) != (departureDate == null)) {
            this.updateContext(context, ERROR_ARRIVAL_AND_DEPARTURE_REQUIRED);
            return false;
        }

        // If both dates are null validation passes
        if (arrivalDate == null) {
            return true;
        }

        if (arrivalDate.isAfter(departureDate)) {
            this.updateContext(context, ERROR_INVALID_DATE_RANGE);
            return false;
        }

        // Validate stay
        final long stay = DAYS.between(arrivalDate, departureDate);
        if (stay < MIN_STAY_DAYS || stay > MAX_STAY_DAYS) {
            this.updateContext(context, ERROR_MIN_MAX_STAY);
            return false;
        }

        // Validate anticipation
        final long anticipation = DAYS.between(LocalDate.now(), arrivalDate);
        final long maxAnticipationDays = DAYS.between(LocalDate.now(), LocalDate.now().plusMonths(MAX_ANTICIPATION_MONTHS));
        if (anticipation < MIN_ANTICIPATION_DAYS || anticipation > maxAnticipationDays) {
            this.updateContext(context, ERROR_MIN_MAX_ANTICIPATION);
            return false;
        }

        return true;
    }

    private void updateContext(ConstraintValidatorContext context, String error) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(error)
                .addConstraintViolation();
    }

}