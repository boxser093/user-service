package net.ilya.users_api_microservice_on_webflux.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class IndividualNonExistentById extends NoSuchElementException {
    private final UUID uuid;
    private static final String MESSAGE = "individual not exist";
}
