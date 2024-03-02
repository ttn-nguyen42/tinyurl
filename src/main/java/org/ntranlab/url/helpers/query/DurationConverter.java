package org.ntranlab.url.helpers.query;


import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
import org.springframework.core.convert.converter.Converter;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public class DurationConverter implements Converter<String, Date> {
    @Override
    public Date convert(@Nonnull String duration) {
        if (duration.isBlank()) {
            throw new BadRequestException("duration must not be empty");
        }
        if (duration.length() < 2) {
            throw new BadRequestException("duration is missing length or unit, must be longer than 2 characters");
        }
        String unit = duration.substring(duration.length() - 1);
        String length = duration.substring(0, duration.length() - 1);
        if (unit.isBlank()) {
            throw new BadRequestException("duration must includes unit");
        }
        if (length.isBlank()) {
            throw new BadRequestException("duration must includes length");
        }
        try {
            int intLength = Integer.parseInt(length);
            if (intLength < 0) {
                throw new BadRequestException("duration length must be greater than or equal to 0");
            }
            TemporalUnit tu = switch (unit) {
                case "s" -> ChronoUnit.SECONDS;
                case "m" -> ChronoUnit.MINUTES;
                case "h" -> ChronoUnit.HOURS;
                case "d" -> ChronoUnit.DAYS;
                case "M" -> ChronoUnit.MONTHS;
                default ->
                        throw new BadRequestException("invalid unit, must be either s (seconds), m (minutes), h (hours), d (days), M (months)");
            };
            return Date.from(
                    Instant.now().minus(
                            intLength,
                            tu
                    ));
        } catch (NumberFormatException e) {
            throw new BadRequestException("duration must be an integer");
        }
    }
}
