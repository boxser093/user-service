package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.entity.Country;
import reactor.core.publisher.Mono;

public interface CountryService extends GenericService<Country,Long> {
    Mono<Country> findByNameAndAlpha2(String nameCountry, String alpha2);
}
