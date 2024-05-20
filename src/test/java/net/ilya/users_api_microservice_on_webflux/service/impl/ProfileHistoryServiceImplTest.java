package net.ilya.users_api_microservice_on_webflux.service.impl;

import net.ilya.users_api_microservice_on_webflux.entity.HistoryReason;
import net.ilya.users_api_microservice_on_webflux.entity.ProfileHistory;
import net.ilya.users_api_microservice_on_webflux.entity.ProfileType;
import net.ilya.users_api_microservice_on_webflux.entity.User;
import net.ilya.users_api_microservice_on_webflux.repository.ProfileHistoryRepository;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProfileHistoryServiceImplTest {
    @Mock
    private ProfileHistoryRepository profileHistoryRepository;
    @InjectMocks
    private ProfileHistoryServiceImpl profileHistoryService;

    @Test
    void findById() {
        //given
        UUID id = UUID.randomUUID();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1().toBuilder()
                .id(id)
                .build();
        //when
        when(profileHistoryRepository.findById(any(UUID.class))).thenReturn(Mono.just(profileHistory1));
        //then
        StepVerifier
                .create(profileHistoryService.findById(id))
                .expectNextMatches(profileHistory -> profileHistory.getId().equals(profileHistory1.getId())
                        && profileHistory.getComment().equals(profileHistory1.getComment()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given

        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ProfileHistory profileHistory = ProfileHistory.builder()
                .id(UUID.randomUUID())
                .created(now)
                .profileId(userId)
                .profileType(ProfileType.INDIVIDUAL)
                .reason(HistoryReason.CREATE.name())
                .comment("Create")
                .changedValues("TEST")
                .build();
        //when
        when(profileHistoryRepository.save(any(ProfileHistory.class))).thenReturn(Mono.just(profileHistory));
        //then
        StepVerifier
                .create(profileHistoryService.create(userId, ProfileType.INDIVIDUAL, HistoryReason.CREATE.name(), "Create", "TEST"))
                .expectNextMatches(history -> history.getId().equals(profileHistory.getId())
                        & history.getCreated().equals(now)
                        & history.getReason().equals(HistoryReason.CREATE.name()))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();
        Flux<ProfileHistory> profileHistoryFlux = Flux.just(profileHistory1);
        //when
        when(profileHistoryRepository.findAll()).thenReturn(profileHistoryFlux);
        //then
        StepVerifier
                .create(profileHistoryService.findAll())
                .expectNext(profileHistory1)
                .expectComplete()
                .verify();
    }
}