package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.entity.Address;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressService extends GenericService<Address, UUID> {
    Mono<Address> findAddressByAddressAndZipCodeAndCityAndState(Address address);
}
