package net.ilya.users_api_microservice_on_webflux.repository;

import net.ilya.users_api_microservice_on_webflux.entity.Address;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AddressRepository extends R2dbcRepository<Address, UUID> {
    @Query("SELECT * FROM person.addresses where address=:ad and zip_code=:zc FOR UPDATE ")
    Mono<Address> findAddressByAddressAndZipCode(String ad, String zc);
}
