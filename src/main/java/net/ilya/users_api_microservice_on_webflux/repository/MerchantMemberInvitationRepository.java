package net.ilya.users_api_microservice_on_webflux.repository;

import net.ilya.users_api_microservice_on_webflux.entity.MerchantMemberInvitations;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantMemberInvitationRepository extends R2dbcRepository<MerchantMemberInvitations, UUID> {
}
