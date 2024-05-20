package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.entity.MerchantMember;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberService extends GenericService<MerchantMember, UUID>{
    Mono<MerchantMember> createNewMerchantMember(UUID invocation);

}
