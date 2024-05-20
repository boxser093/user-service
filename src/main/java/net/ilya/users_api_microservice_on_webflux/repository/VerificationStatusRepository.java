package net.ilya.users_api_microservice_on_webflux.repository;

import net.ilya.users_api_microservice_on_webflux.entity.VerificationStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface VerificationStatusRepository extends R2dbcRepository<VerificationStatus, UUID> {
    @Query("SELECT * FROM person.verification_statuses where profile_id=:profileId FOR UPDATE")
    Mono<VerificationStatus> findByProfileId(UUID profileId);
}
