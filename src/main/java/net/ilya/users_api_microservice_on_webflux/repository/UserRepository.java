package net.ilya.users_api_microservice_on_webflux.repository;

import net.ilya.users_api_microservice_on_webflux.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {
    @Query("SELECT * FROM person.users where first_name=:firstName and last_name=:lastName and secret_key=:secretKey FOR UPDATE")
    Mono<User> findUserByFirstNameAndLastNameAAndSecretKey(String firstName, String lastName, String secretKey);
}
