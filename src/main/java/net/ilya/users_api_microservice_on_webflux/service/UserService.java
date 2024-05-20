package net.ilya.users_api_microservice_on_webflux.service;

import net.ilya.users_api_microservice_on_webflux.entity.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService extends GenericService<User, UUID> {
    Mono<User> verified(VerificationStatus verificationStatus);
}
