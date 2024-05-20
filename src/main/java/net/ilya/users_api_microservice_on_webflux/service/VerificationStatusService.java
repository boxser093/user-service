package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.entity.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationStatusService extends GenericService<VerificationStatus, UUID>{
    Mono<VerificationStatus> unverifiedIndividual(User user);
    Mono<VerificationStatus> unverifiedMerchant(User user);
    Mono<VerificationStatus> unverifiedMerchantMember(User user);
    Mono<VerificationStatus> verifiedIndividual(Individual individual);
    Mono<VerificationStatus> verifiedMerchant(Merchant merchant);
    Mono<VerificationStatus> verifiedMerchantMember(MerchantMember merchantMember);
}
