package net.ilya.users_api_microservice_on_webflux.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.ilya.users_api_microservice_on_webflux.entity.Individual;

import java.util.NoSuchElementException;
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class IndividualNotUniq extends NoSuchElementException {
    private final Individual individual;
    private static final String MESSAGE = "individual not uniq";
}

