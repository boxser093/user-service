package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.entity.Individual;
import net.ilya.users_api_microservice_on_webflux.entity.ProfileHistory;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualService extends GenericService<Individual, UUID> {
    Mono<?> saveIndividual(IndividualDto individualDto);

    Mono<?> deletedIndividual(UUID uuid);

    Mono<?> updateIndividual(IndividualDto individualDto);

    Mono<?> verified(IndividualDto individualDto);

    Mono<Individual> updateVerifiedDate(ProfileHistory profileHistory);

}
