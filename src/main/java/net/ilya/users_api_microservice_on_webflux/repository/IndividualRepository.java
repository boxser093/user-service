package net.ilya.users_api_microservice_on_webflux.repository;

import net.ilya.users_api_microservice_on_webflux.entity.Individual;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IndividualRepository extends R2dbcRepository<Individual, UUID> {
    @Query("SELECT * FROM person.individuals where email=:email and passport_number=:passportNumber and phone_number=:phoneNumber FOR UPDATE")
    Mono<Individual> findByEmailAndPassportNumberAndPhoneNumber(String email, String passportNumber, String phoneNumber);
    @Query("SELECT * FROM person.individuals where email=:email FOR UPDATE")
    Mono<Individual> findByEmail(String email);
    @Query("SELECT * FROM person.individuals where user_id=:userId FOR UPDATE")
    Mono<Individual> findIndividualByUserId(UUID userId);
}
