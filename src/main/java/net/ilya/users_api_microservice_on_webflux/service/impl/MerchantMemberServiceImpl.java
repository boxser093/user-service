package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.error.ObjectNotExist;
import net.ilya.users_api_microservice_on_webflux.repository.MerchantMemberRepository;
import net.ilya.users_api_microservice_on_webflux.service.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantMemberServiceImpl implements MerchantMemberService {
    private final MerchantMemberRepository merchantMemberRepository;
    private final MerchantMemberInvitationService merchantMemberInvitationService;
    private final UserService userService;
    private final ProfileHistoryService profileHistoryService;
    private final VerificationStatusService verificationStatusService;

    @Override
    public Mono<MerchantMember> findById(UUID uuid) {
        log.info("In MerchantMemberServiceImpl findById - {}", uuid);
        return merchantMemberRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())));
    }

    @Override
    public Mono<MerchantMember> create(MerchantMember merchantMember) {
        log.info("In MerchantMemberServiceImpl findById - {}", merchantMember);
        return merchantMemberRepository.save(merchantMember.toBuilder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .build());
    }

    @Override
    public Mono<MerchantMember> update(MerchantMember merchantMember) {
        log.info("In MerchantMemberServiceImpl findById - {}", merchantMember);
        return merchantMemberRepository.findById(merchantMember.getId())
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(merchantMember.getId().toString())))
                .flatMap(memberExist -> userService.update(merchantMember.getUser())
                        .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.MERCHANT_MEMBER, HistoryReason.UPDATE.name(), String.format("Update merchant member %s, %s, %s", user.getFirstName(), user.getLastName(), merchantMember.getMemberRole()), merchantMember.toString())
                                .flatMap(profileHistory -> merchantMemberRepository.save(memberExist.toBuilder()
                                        .memberRole(merchantMember.getMemberRole())
                                        .updated(LocalDateTime.now())
                                        .status(StatusEntity.UPDATED)
                                        .build()))));
    }

    @Override
    public Mono<MerchantMember> deleted(UUID uuid) {
        log.info("In MerchantMemberServiceImpl findById - {}", uuid);
        return merchantMemberRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())))
                .flatMap(merchantMember -> userService.deleted(merchantMember.getUserId())
                        .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.MERCHANT_MEMBER, HistoryReason.DELETED.name(), String.format("Delete user and merchant member by id:%s", uuid), uuid.toString())
                                .flatMap(profileHistory -> merchantMemberRepository.save(merchantMember.toBuilder()
                                        .updated(LocalDateTime.now())
                                        .status(StatusEntity.DELETED)
                                        .build()))));
    }

    @Override
    public Flux<MerchantMember> findAll() {
        log.info("In MerchantMemberServiceImpl findAll ");
        return merchantMemberRepository.findAll();
    }

    @Override
    public Mono<MerchantMember> createNewMerchantMember(UUID invocation) {
        log.info("In MerchantMemberServiceImpl createNewMerchantMember by invocation ID: {}", invocation);
        return merchantMemberInvitationService.findById(invocation)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(invocation.toString())))
                .flatMap(merchantMemberInvitationService::checkInvitation)
                .flatMap(merchantMemberInvitations -> userService.create(User.builder()
                                .firstName(merchantMemberInvitations.getFirstName())
                                .lastName(merchantMemberInvitations.getLastName())
                                .build())
                        .flatMap(verificationStatusService::unverifiedMerchant)
                        .flatMap(verificationStatus -> profileHistoryService.create(verificationStatus.getProfileId(), ProfileType.MERCHANT_MEMBER, HistoryReason.CREATE.name(), "Create new merchant Member", String.valueOf(invocation))
                                .flatMap(profileHistory -> this.create(MerchantMember.builder()
                                        .merchantId(merchantMemberInvitations.getMerchantId())
                                        .memberRole(MemberRole.MANAGER)
                                        .userId(profileHistory.getProfileId())
                                        .build()))));
    }
}
