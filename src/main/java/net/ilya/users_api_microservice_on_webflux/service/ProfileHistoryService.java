package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.entity.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProfileHistoryService {
    Mono<ProfileHistory> findById(UUID v);

    Mono<ProfileHistory> create(UUID uuid, ProfileType profileType, String reason, String comment, String changeValues);
    Flux<ProfileHistory> findAll();
}
