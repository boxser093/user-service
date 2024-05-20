package net.ilya.users_api_microservice_on_webflux.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class UserNotUniq extends NoSuchElementException {
    private final String user;
    private static final String MESSAGE = "User not uniq";
}
