package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.dto.MerchantDto;
import net.ilya.users_api_microservice_on_webflux.entity.Merchant;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantService extends GenericService<Merchant, UUID> {
    Mono<Merchant> verifiedMerchant(Merchant merchant);
}
