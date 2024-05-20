package net.ilya.users_api_microservice_on_webflux.service.impl;

import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;
import net.ilya.users_api_microservice_on_webflux.entity.User;
import net.ilya.users_api_microservice_on_webflux.entity.VerificationStatus;
import net.ilya.users_api_microservice_on_webflux.repository.UserRepository;
import net.ilya.users_api_microservice_on_webflux.util.DateUtilsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findById() {
        //given
        User user1 = DateUtilsService.getUser1();
        UUID id = user1.getId();
        //when
        when(userRepository.findById(id)).thenReturn(Mono.just(user1));
        //then
        StepVerifier
                .create(userService.findById(id))
                .expectNextMatches(user -> user.getId().equals(user1.getId())
                        && user.getFirstName().equals(user1.getFirstName()))
                .expectComplete()
                .verify();

    }

    @Test
    void create() {
        //given
        User user = DateUtilsService.getUser1();
        //when
        when(userRepository.findUserByFirstNameAndLastNameAAndSecretKey(anyString(),anyString(),anyString())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user.toBuilder()
                .created(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .filled(false)
                .build()));
        //then
        StepVerifier
                .create(userService.create(user))
                .expectNextMatches(user1 -> user1.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();

    }

    @Test
    void update() {
        //given
        User user = DateUtilsService.getUser2();
        UUID id = user.getId();
        //when
        when(userRepository.findById(id)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user.toBuilder()
                .updated(LocalDateTime.now())
                .status(StatusEntity.UPDATED)
                .build()));
        //then
        StepVerifier.
                create(userService.update(user))
                .expectNextMatches(user1 -> user1.getId().equals(user.getId())
                        && user1.getStatus().equals(StatusEntity.UPDATED))
                .expectComplete()
                .verify();
    }

    @Test
    void softDelete() {
        //given
        User user = DateUtilsService.getUser2();
        UUID id = user.getId();
        //when
        when(userRepository.findById(id)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user.toBuilder()
                .status(StatusEntity.DELETED)
                .build()));
        //then
        StepVerifier
                .create(userService.deleted(id))
                .expectNextMatches(user1 -> user1.getId().equals(user.getId())
                        && user1.getStatus().equals(StatusEntity.DELETED))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        User user1 = DateUtilsService.getUser1();
        User user2 = DateUtilsService.getUser2();
        Flux<User> users = Flux.just(user1, user2);
        //when
        when(userRepository.findAll()).thenReturn(users);
        //then
        StepVerifier
                .create(userService.findAll())
                .expectNext(user1)
                .expectNext(user2)
                .expectComplete()
                .verify();
    }


    @Test
    void verified() {
        //given

        User user = DateUtilsService.getUser1().toBuilder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .filled(true)
                .build();
        VerificationStatus verificationStatusForUser1 = DateUtilsService.getVerificationStatusForUser1().toBuilder()
                .profileId(user.getId())
                .build();

        //when
        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user.toBuilder()
                .updated(verificationStatusForUser1.getUpdated())
                .status(StatusEntity.UPDATED)
                .build()));
        //then
        StepVerifier
                .create(userService.verified(verificationStatusForUser1))
                .expectNextMatches(user1 -> user1.getStatus().equals(StatusEntity.UPDATED))
                .expectComplete()
                .verify();
    }

}