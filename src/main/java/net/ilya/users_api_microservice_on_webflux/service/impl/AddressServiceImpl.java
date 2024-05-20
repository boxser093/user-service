package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.entity.Address;
import net.ilya.users_api_microservice_on_webflux.entity.Country;
import net.ilya.users_api_microservice_on_webflux.error.DuplicateResourceException;
import net.ilya.users_api_microservice_on_webflux.error.ObjectNotExist;
import net.ilya.users_api_microservice_on_webflux.repository.AddressRepository;
import net.ilya.users_api_microservice_on_webflux.service.AddressService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    public Mono<Address> findById(UUID uuid) {
        return addressRepository.findById(uuid);
    }

    @Override
    public Mono<Address> create(Address address) {
        return addressRepository.findAddressByAddressAndZipCode(address.getAddress(), address.getZipCode())
                .flatMap(addressExist -> Mono.error(new DuplicateResourceException(address.toString())))
                .switchIfEmpty(Mono.defer(() -> addressRepository.save(address.toBuilder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .archived(LocalDateTime.now())
                        .build()))).cast(Address.class);
    }

    @Override
    public Mono<Address> update(Address address) {
        return addressRepository.findById(address.getId())
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(address.getId().toString())))
                .map(address1 -> address1.toBuilder()
                        .updated(LocalDateTime.now())
                        .address(address.getAddress())
                        .zipCode(address.getZipCode())
                        .city(address.getCity())
                        .state(address.getState())
                        .build())
                .flatMap(addressRepository::save);
    }

    @Override
    public Mono<Address> deleted(UUID uuid) {
        return findById(uuid).
                map(address -> address.toBuilder()
                        .archived(LocalDateTime.now())
                        .build())
                .flatMap(addressRepository::save);
    }

    @Override
    public Flux<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public Mono<Address> findByAddressAndZipCode(String address, String zipCode) {
        return addressRepository.findAddressByAddressAndZipCode(address, zipCode);
    }

}
