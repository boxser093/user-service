package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.entity.MerchantMemberInvitations;
import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;
import net.ilya.users_api_microservice_on_webflux.error.InvitationExpireError;
import net.ilya.users_api_microservice_on_webflux.error.ObjectNotExist;
import net.ilya.users_api_microservice_on_webflux.repository.MerchantMemberInvitationRepository;
import net.ilya.users_api_microservice_on_webflux.service.MerchantMemberInvitationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantMemberInvitationServiceImpl implements MerchantMemberInvitationService {
    private final MerchantMemberInvitationRepository merchantMemberInvitationRepository;

    @Override
    public Mono<MerchantMemberInvitations> findById(UUID uuid) {
        log.info("# In MerchantMemberInvitationServiceImpl findById -{}", uuid);
        return merchantMemberInvitationRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())));
    }

    @Override
    public Mono<MerchantMemberInvitations> create(MerchantMemberInvitations merchantMemberInvitations) {
        log.info("# In MerchantMemberInvitationServiceImpl create -{}", merchantMemberInvitations);
        return merchantMemberInvitationRepository.save(merchantMemberInvitations.toBuilder()
                .status(StatusEntity.ACTIVE)
                .created(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<MerchantMemberInvitations> update(MerchantMemberInvitations merchantMemberInvitations) {
        log.info("# In MerchantMemberInvitationServiceImpl create -{}", merchantMemberInvitations);
        return merchantMemberInvitationRepository.findById(merchantMemberInvitations.getId())
                .switchIfEmpty(Mono.error(()-> new ObjectNotExist(merchantMemberInvitations.getId().toString())))
                .map(merchantMemberInvitations1 -> merchantMemberInvitations1.toBuilder()
                        .expires(merchantMemberInvitations.getExpires())
                        .firstName(merchantMemberInvitations.getFirstName())
                        .lastName(merchantMemberInvitations.getLastName())
                        .email(merchantMemberInvitations.getEmail())
                        .status(StatusEntity.UPDATED)
                        .build())
                .flatMap(merchantMemberInvitationRepository::save);
    }

    @Override
    public Mono<MerchantMemberInvitations> deleted(UUID uuid) {
        log.info("# In MerchantMemberInvitationServiceImpl findAll");
        return merchantMemberInvitationRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())))
                .flatMap(merchantMemberInvitations -> merchantMemberInvitationRepository.save(merchantMemberInvitations.toBuilder()
                        .status(StatusEntity.DELETED)
                        .build()));
    }

    @Override
    public Flux<MerchantMemberInvitations> findAll() {
        log.info("# In MerchantMemberInvitationServiceImpl findAll");
        return merchantMemberInvitationRepository.findAll();
    }

    @Override
    public Mono<MerchantMemberInvitations> checkInvitation(MerchantMemberInvitations merchantMemberInvitations) {
       if (merchantMemberInvitations.getStatus().equals(StatusEntity.ACTIVE) & merchantMemberInvitations.getExpires().isAfter(LocalDateTime.now())){
           return Mono.just(merchantMemberInvitations);
       }
        return Mono.error(()-> new InvitationExpireError(merchantMemberInvitations.getId()));
    }
}
