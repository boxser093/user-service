package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.entity.MerchantMemberInvitations;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberInvitationService extends GenericService<MerchantMemberInvitations, UUID> {
     Mono<MerchantMemberInvitations> checkInvitation(MerchantMemberInvitations merchantMemberInvitations);
}
