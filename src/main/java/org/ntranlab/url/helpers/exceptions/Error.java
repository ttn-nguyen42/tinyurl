package org.ntranlab.url.helpers.exceptions;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Error {
    private Date timestamp;
    private String message;

    @JsonIgnore
    @ToString.Exclude
    private Exception exception;
}
