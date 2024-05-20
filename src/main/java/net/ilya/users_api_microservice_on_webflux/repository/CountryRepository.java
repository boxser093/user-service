package net.ilya.users_api_microservice_on_webflux.repository;

import net.ilya.users_api_microservice_on_webflux.entity.Country;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CountryRepository extends R2dbcRepository<Country, Long> {
    @Query("SELECT * FROM person.countries where name=:name and alpha2=:alpha2 FOR UPDATE")
    Mono<Country> findCountryByNameAndAlpha2(String name, String alpha2);
}
