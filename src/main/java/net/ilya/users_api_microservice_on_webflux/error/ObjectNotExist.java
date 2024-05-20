package net.ilya.users_api_microservice_on_webflux.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.UUID;
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class ObjectNotExist extends NoSuchElementException {
    private final String uuid;
    private static final String MESSAGE = "Object not exist";
}
