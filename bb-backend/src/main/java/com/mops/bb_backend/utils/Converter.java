package com.mops.bb_backend.utils;

import com.mops.bb_backend.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Converter {
    public static UUID convertStringToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "The provided ID is not in a valid UUID format!");
        }
    }

    public static String formatDate(LocalDate date) {
        var formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        return date.format(formatter);
    }
}
