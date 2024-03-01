package org.ntranlab.url.helpers.exceptions;

import java.time.Instant;
import java.util.Date;

import org.ntranlab.url.helpers.exceptions.types.AlreadyExistsException;
import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
import org.ntranlab.url.helpers.exceptions.types.InternalServerException;
import org.ntranlab.url.helpers.exceptions.types.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalHandler extends ResponseEntityExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalHandler.class);

    @ExceptionHandler(value = { BadRequestException.class })
    protected ResponseEntity<Error> handleBadRequestException(BadRequestException e) {
        Error error = this.getBaseError(e);
        this.log(error);
        return new ResponseEntity<>(
                error,
                HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(value = { NotFoundException.class })
    protected ResponseEntity<Error> handleNotFoundException(NotFoundException e) {
        Error error = this.getBaseError(e);
        this.log(error);
        return new ResponseEntity<>(
                error,
                HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler(value = { InternalServerException.class, InternalError.class })
    protected ResponseEntity<Error> handleInternalServerException(Exception e) {
        Error error = this.getBaseError(e);
        this.log(error);
        return new ResponseEntity<>(
                error,
                HttpStatusCode.valueOf(500));
    }

    @ExceptionHandler(value = { AlreadyExistsException.class })
    protected ResponseEntity<Error> handleAlreadyExistsException(AlreadyExistsException e) {
        Error error = this.getBaseError(e);
        this.log(error);
        return new ResponseEntity<>(
                error,
                HttpStatusCode.valueOf(409));
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Error> handleException(Exception e) {
        Error error = this.getBaseError(e);
        this.log(error);
        return new ResponseEntity<>(
                error,
                HttpStatusCode.valueOf(500));
    }

    private void log(Error error) {
        logger.warn("GlobalHandler: error = " + error.toString());
    }

    private Error getBaseError(Exception e) {
        return Error
                .builder()
                .message(e.getMessage())
                .timestamp(Date.from(Instant.now()))
                .exception(e)
                .build();
    }
}
