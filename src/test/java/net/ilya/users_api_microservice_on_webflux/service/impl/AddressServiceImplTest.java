package net.ilya.users_api_microservice_on_webflux.service.impl;

import net.ilya.users_api_microservice_on_webflux.entity.Address;
import net.ilya.users_api_microservice_on_webflux.entity.Country;
import net.ilya.users_api_microservice_on_webflux.repository.AddressRepository;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;
    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void findByAddressAndZipCode() {
        //given
        Address address = DateUtilsService.getAddress1();
        String address2 = address.getAddress();
        String zipCode = address.getZipCode();
        //when
        when(addressRepository.findAddressByAddressAndZipCode(anyString(), anyString()))
                .thenReturn(Mono.just(address));
        //then
        StepVerifier
                .create(addressService.findByAddressAndZipCode(address2, zipCode))
                .expectNextMatches(address1 ->
                        address1.getAddress().equals((address2))
                                && address1.getZipCode().equals(zipCode))
                .expectComplete()
                .verify();
    }

    @Test
    void findById() {
        //given
        Address address = DateUtilsService.getAddress1();
        UUID id = address.getId();
        //when
        when(addressRepository.findById(id)).thenReturn(Mono.just(address));
        //then
        StepVerifier
                .create(addressService.findById(id))
                .expectNextMatches(address1 -> address1.getId().equals(address.getId())
                        && address1.getAddress().equals((address.getAddress()))
                        && address1.getCity().equals(address.getCity()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given
        Address address = DateUtilsService.getAddress2();
        UUID id = UUID.randomUUID();
        //when
        when(addressRepository.findAddressByAddressAndZipCode(anyString(),anyString())).thenReturn(Mono.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address.toBuilder()
                .id(id)
                .created(LocalDateTime.now())
                .build()));
        //then
        StepVerifier
                .create(addressService.create(address))
                .expectNextMatches(address1 -> address1.getId().equals(id)
                        && address1.getAddress().equals((address.getAddress()))
                        && address1.getCity().equals(address.getCity()))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given
        Address address1 = DateUtilsService.getAddress1();
        //when
        when(addressRepository.findById(address1.getId())).thenReturn(Mono.just(address1));
        when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address1.toBuilder()
                .updated(LocalDateTime.now())
                .countryId(2L)
                .city("Moscow")
                .build()));
        //then
        StepVerifier
                .create(addressService.update(address1))
                .expectNextMatches(address -> address.getId().equals(address1.getId())
                        && address.getCity().equals("Moscow"))
                .expectComplete()
                .verify();
    }

    @Test
    void softDelete() {
        //given
        Address address1 = DateUtilsService.getAddress1();
        UUID id = address1.getId();
        LocalDateTime archived = LocalDateTime.now();
        //when
        when(addressRepository.findById(address1.getId())).thenReturn(Mono.just(address1));
        when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address1.toBuilder()
                .archived(archived)
                .build()));
        //then
        StepVerifier
                .create(addressService.deleted(id))
                .expectNextMatches(address -> address.getId().equals(address1.getId())
                        && address.getArchived().equals(archived))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        Address address1 = DateUtilsService.getAddress1();
        Address address2 = DateUtilsService.getAddress2().toBuilder()
                .id(UUID.randomUUID())
                .build();
        Flux<Address> returned = Flux.just(address1, address2);
        //when
        when(addressRepository.findAll()).thenReturn(returned);
        //then
        StepVerifier
                .create(addressService.findAll())
                .expectNext(address1)
                .expectNext(address2)
                .verifyComplete();
    }

}