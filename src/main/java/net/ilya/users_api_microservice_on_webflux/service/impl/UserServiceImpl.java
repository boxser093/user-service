package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.error.DuplicateResourceException;
import net.ilya.users_api_microservice_on_webflux.error.ObjectNotExist;
import net.ilya.users_api_microservice_on_webflux.repository.UserRepository;
import net.ilya.users_api_microservice_on_webflux.repository.VerificationStatusRepository;
import net.ilya.users_api_microservice_on_webflux.service.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<User> findById(UUID uuid) {
        log.info("In UserServiceImpl findById -{}", uuid);
        return userRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())));
    }

    @Override
    public Mono<User> create(User user) {
        log.info("In UserServiceImpl create -{}", user);
        return userRepository.findUserByFirstNameAndLastNameAAndSecretKey(user.getFirstName(), user.getLastName(), user.getSecretKey())
                .flatMap(userExist -> Mono.error(new DuplicateResourceException(String.format("%s,%s,%s", user.getFirstName(), user.getLastName(), user.getSecretKey()))))
                .switchIfEmpty(Mono.defer(() -> userRepository.save(user.toBuilder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .verifiedAt(LocalDateTime.now())
                        .archivedAt(LocalDateTime.now())
                        .status(StatusEntity.ACTIVE)
                        .filled(false)
                        .build()))).cast(User.class);
    }

    @Override
    public Mono<User> update(User user) {
        log.info("In UserServiceImpl update -{}", user);
        return userRepository.findById(user.getId())
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(user.getId().toString())))
                .map(user1 -> user1.toBuilder()
                        .secretKey(user.getSecretKey())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .status(StatusEntity.UPDATED)
                        .updated(LocalDateTime.now())
                        .build())
                .flatMap(userRepository::save);
    }

    @Override
    public Mono<User> deleted(UUID uuid) {
        log.info("In UserServiceImpl deleted -{}", uuid);
        return userRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())))
                .map(user -> user.toBuilder()
                        .archivedAt(LocalDateTime.now())
                        .status(StatusEntity.DELETED)
                        .build())
                .flatMap(userRepository::save);
    }

    @Override
    public Flux<User> findAll() {
        log.info("In UserServiceImpl findAll");
        return userRepository.findAll();
    }


    @Override
    public Mono<User> verified(VerificationStatus verificationStatus) {
        log.info("In UserServiceImpl verified -{}", verificationStatus);
        return userRepository.findById(verificationStatus.getProfileId())
                .map(user -> user.toBuilder()
                        .verifiedAt(verificationStatus.getUpdated())
                        .build())
                .flatMap(userRepository::save);
    }

}
