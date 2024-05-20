package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.repository.ProfileHistoryRepository;
import net.ilya.users_api_microservice_on_webflux.service.ProfileHistoryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileHistoryServiceImpl implements ProfileHistoryService {
    private final ProfileHistoryRepository profileHistoryRepository;

    @Override
    public Mono<ProfileHistory> findById(UUID uuid) {
        return profileHistoryRepository.findById(uuid);
    }

    @Override
    public Mono<ProfileHistory> create(UUID uuid, ProfileType profileType, String reason, String comment, String changeValues) {
        return profileHistoryRepository.save(ProfileHistory.builder()
                        .created(LocalDateTime.now())
                        .profileId(uuid)
                        .profileType(profileType)
                        .reason(reason)
                        .comment(comment)
                        .changedValues(changeValues)
                        .build());
    }


    @Override
    public Flux<ProfileHistory> findAll() {
        return profileHistoryRepository.findAll();
    }


}
