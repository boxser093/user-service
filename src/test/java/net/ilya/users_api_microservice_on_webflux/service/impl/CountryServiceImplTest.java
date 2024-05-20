package net.ilya.users_api_microservice_on_webflux.service.impl;

import net.ilya.users_api_microservice_on_webflux.entity.Country;
import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;
import net.ilya.users_api_microservice_on_webflux.repository.CountryRepository;
import net.ilya.users_api_microservice_on_webflux.util.DateUtilsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private CountryServiceImpl countryService;

    @Test
    void findByNameAndAlpha2() {
        Country country1 = DateUtilsService.getCountry1();
        String name = country1.getName();
        String alpha2 = country1.getAlpha2();
        //when
        when(countryRepository.findCountryByNameAndAlpha2(anyString(), anyString()))
                .thenReturn(Mono.just(country1));
        //then
        StepVerifier
                .create(countryService.findByNameAndAlpha2(name,alpha2))
                .expectNextMatches(country -> country.getId().equals(country1.getId())
                        && country.getName().equals((name))
                        && country.getAlpha2().equals(alpha2))
                .expectComplete()
                .verify();
    }

    @Test
    void findById() {
        Country country1 = DateUtilsService.getCountry1();
        //when
        when(countryRepository.findById(1L)).thenReturn(Mono.just(country1));
        //then
        StepVerifier
                .create(countryService.findById(1L))
                .expectNextMatches(country -> country.getId().equals(country1.getId())
                        && country.getName().equals((country1.getName()))
                        && country.getAlpha2().equals(country1.getAlpha2()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given
        Country after = DateUtilsService.getCountryForCreatedMethodAfterEntity();

        Country before = DateUtilsService.getCountryForCreatedMethodBeforeEntity();
        //when
        when(countryRepository.findCountryByNameAndAlpha2(anyString(),anyString())).thenReturn(Mono.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(Mono.just(before));
        //then
        StepVerifier
                .create(countryService.create(after))
                .expectNextMatches(country -> country.getId().equals(before.getId())
                        && country.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();

    }

    @Test
    void update() {
        //given
        Country beforeUpdate = DateUtilsService.getCountryForCreatedMethodBeforeEntity();

        Country after = DateUtilsService.getCountryForCreatedMethodBeforeEntity().toBuilder()
                .name("for test")
                .updated(LocalDateTime.now())
                .status(StatusEntity.UPDATED)
                .alpha2("TSTS")
                .build();
        //when
        when(countryRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(countryRepository.save(any(Country.class))).thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(countryService.update(beforeUpdate))
                .expectNextMatches(country -> country.getId().equals(after.getId())
                        && country.getName().equals(after.getName())
                        && country.getAlpha2().equals("TSTS")
                        && country.getStatus().equals(StatusEntity.UPDATED))
                .expectComplete()
                .verify();

    }

    @Test
    void softDeleted() {
        Country country = DateUtilsService.getCountryForCreatedMethodBeforeEntity();
        //when
        when(countryRepository.findById(country.getId())).thenReturn(Mono.just(country));
        when(countryRepository.save(any(Country.class))).thenReturn(Mono.just(country.toBuilder()
                .status(StatusEntity.DELETED)
                .build()));
        //then
        StepVerifier
                .create(countryService.update(country))
                .expectNextMatches(country1 -> country1.getId().equals(country.getId())
                        && country1.getStatus().equals(StatusEntity.DELETED))
                .expectComplete()
                .verify();

    }

    @Test
    void findAll() {
        //given
        Country country1 = DateUtilsService.getCountry1();
        Country country2 = DateUtilsService.getCountryForCreatedMethodBeforeEntity();
        Flux<Country> justAll = Flux.just(country1, country2);
        //when
        when(countryRepository.findAll()).thenReturn(justAll);
        //then
        StepVerifier
                .create(countryService.findAll())
                .expectNext(country1)
                .expectNext(country2)
                .verifyComplete();

    }
}