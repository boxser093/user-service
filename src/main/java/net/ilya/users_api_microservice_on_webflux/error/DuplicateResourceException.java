package net.ilya.users_api_microservice_on_webflux.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class DuplicateResourceException  extends NoSuchElementException {
    private final String errorEntity;
    private static final String MESSAGE = "Such data is already in the database";
}
