package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.entity.Country;
import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;
import net.ilya.users_api_microservice_on_webflux.error.DuplicateResourceException;
import net.ilya.users_api_microservice_on_webflux.error.ObjectNotExist;
import net.ilya.users_api_microservice_on_webflux.repository.CountryRepository;
import net.ilya.users_api_microservice_on_webflux.service.CountryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public Mono<Country> findById(Long aLong) {
        return countryRepository.findById(aLong);
    }

    @Override
    public Mono<Country> create(Country country) {
        return Mono.just(country).flatMap(this::findByNameAndAlpha2AndAlpha3)
                .switchIfEmpty(Mono.defer(() -> countryRepository.save(country.toBuilder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .status(StatusEntity.ACTIVE)
                        .build()))).cast(Country.class);
    }

    @Override
    public Mono<Country> update(Country country) {
        return countryRepository.findById(country.getId())
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(country.getId().toString())))
                .map(country1 -> country1.toBuilder()
                        .updated(LocalDateTime.now())
                        .name(country.getName())
                        .alpha2(country.getAlpha2())
                        .alpha3(country.getAlpha3())
                        .status(StatusEntity.UPDATED)
                        .build())
                .flatMap(countryRepository::save);
    }

    @Override
    public Mono<Country> deleted(Long aLong) {
        return countryRepository.findById(aLong)
                .map(country -> country.toBuilder()
                        .status(StatusEntity.DELETED)
                        .build())
                .flatMap(countryRepository::save);
    }

    @Override
    public Flux<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public Mono<Country> findByNameAndAlpha2AndAlpha3(Country country) {
        return countryRepository.findCountryByNameAndAlpha2AAndAlpha3(country.getName(),country.getAlpha2(),country.getAlpha3());
    }

}
