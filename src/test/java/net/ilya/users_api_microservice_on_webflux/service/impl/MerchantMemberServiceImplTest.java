package net.ilya.users_api_microservice_on_webflux.service.impl;

import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.repository.MerchantMemberRepository;
import net.ilya.users_api_microservice_on_webflux.service.MerchantMemberInvitationService;
import net.ilya.users_api_microservice_on_webflux.service.ProfileHistoryService;
import net.ilya.users_api_microservice_on_webflux.service.UserService;
import net.ilya.users_api_microservice_on_webflux.service.VerificationStatusService;
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

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantMemberServiceImplTest {
    @Mock
    private MerchantMemberRepository merchantMemberRepository;
    @Mock
    private MerchantMemberInvitationService merchantMemberInvitationService;
    @Mock
    private UserService userService;
    @Mock
    private ProfileHistoryService profileHistoryService;
    @Mock
    private VerificationStatusService verificationStatusService;
    @InjectMocks
    private MerchantMemberServiceImpl merchantMemberService;

    @Test
    void findById() {
        //given
        UUID memberId = UUID.randomUUID();
        MerchantMember merchantMember = DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .id(memberId)
                .build();
        //when
        when(merchantMemberRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantMember));
        //then
        StepVerifier.create(merchantMemberService.findById(memberId))
                .expectNextMatches(merchantMember1 -> merchantMember1.getId().equals(memberId))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given
        UUID memberId = UUID.randomUUID();
        MerchantMember merchantMember = DateUtilsService.getMerchantMemberWithDateActive();
        //when
        when(merchantMemberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember.toBuilder()
                .id(memberId)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .build()));
        //then
        StepVerifier.create(merchantMemberService.create(merchantMember))
                .expectNextMatches(merchantMember1 -> merchantMember1.getId().equals(memberId) &
                        merchantMember1.getStatus().equals(StatusEntity.ACTIVE) &
                        merchantMember1.getCreated().isBefore(LocalDateTime.now()))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given
        UUID memberId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = DateUtilsService.getUserWithDate().toBuilder()
                .id(userId)
                .firstName("Update 1")
                .lastName("Update 2")
                .build();
        MerchantMember merchantMember = DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .id(memberId)
                .user(user)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();
        //when
        when(merchantMemberRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantMember));
        when(userService.update(any(User.class))).thenReturn(Mono.just(user.toBuilder()
                .id(userId)
                .updated(LocalDateTime.now())
                .status(StatusEntity.UPDATED)
                .firstName("Update 1")
                .lastName("Update 2")
                .build()));
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .profileId(user.getId())
                .reason(HistoryReason.UPDATE.name())
                .profileType(ProfileType.MERCHANT_MEMBER)
                .changedValues(merchantMember.toString())
                .build()));
        when(merchantMemberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember.toBuilder()
                .id(memberId)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .status(StatusEntity.UPDATED)
                .build()));
        //then
        StepVerifier.create(merchantMemberService.update(merchantMember))
                .expectNextMatches(merchantMember1 -> merchantMember1.getId().equals(memberId) &
                        merchantMember1.getStatus().equals(StatusEntity.UPDATED) &
                        merchantMember1.getUpdated().isBefore(LocalDateTime.now()))
                .expectComplete()
                .verify();
    }

    @Test
    void deleted() {
        //given
        UUID memberId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = DateUtilsService.getUserWithDate().toBuilder()
                .id(userId)
                .firstName("Update 1")
                .lastName("Update 2")
                .build();
        MerchantMember merchantMember = DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .id(memberId)
                .userId(user.getId())
                .user(user)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();
        //when
        when(merchantMemberRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantMember));
        when(userService.deleted(any(UUID.class))).thenReturn(Mono.just(user.toBuilder()
                .id(userId)
                .updated(LocalDateTime.now())
                .status(StatusEntity.DELETED)
                .firstName("Deleted 1")
                .lastName("Deleted 2")
                .build()));
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .profileId(user.getId())
                .reason(HistoryReason.DELETED.name())
                .profileType(ProfileType.MERCHANT_MEMBER)
                .changedValues(merchantMember.toString())
                .build()));
        when(merchantMemberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember.toBuilder()
                .id(memberId)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .status(StatusEntity.DELETED)
                .build()));
        //then
        StepVerifier.create(merchantMemberService.deleted(memberId))
                .expectNextMatches(merchantMember1 -> merchantMember1.getId().equals(memberId) &
                        merchantMember1.getStatus().equals(StatusEntity.DELETED) &
                        merchantMember1.getUpdated().isBefore(LocalDateTime.now()))
                .expectComplete()
                .verify();

    }

    @Test
    void findAll() {
        //given
        MerchantMember merchantMemberWithDateActive1 = DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .id(UUID.randomUUID())
                .build();
        MerchantMember merchantMemberWithDateActive2 = DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .id(UUID.randomUUID())
                .build();
        //when
        when(merchantMemberRepository.findAll()).thenReturn(Flux.just(merchantMemberWithDateActive1, merchantMemberWithDateActive2));
        //then
        StepVerifier.create(merchantMemberService.findAll())
                .expectNext(merchantMemberWithDateActive1)
                .expectNext(merchantMemberWithDateActive2)
                .expectComplete()
                .verify();
    }

    @Test
    void createNewMerchantMember() {
        //given
        UUID invitationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID statusVerifyId = UUID.randomUUID();
        UUID historyId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        MerchantMember merchantMember = DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .id(memberId)
                .build();
        MerchantMemberInvitations invitations = DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .id(invitationId)
                .build();
        User user = DateUtilsService.getUserWithDate().toBuilder()
                .id(userId)
                .firstName(invitations.getFirstName())
                .lastName(invitations.getLastName())
                .build();
        VerificationStatus verificationStatusForUser1 = DateUtilsService.getVerificationStatusForUser1();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();

        //when
        when(merchantMemberInvitationService.findById(any(UUID.class))).thenReturn(Mono.just(invitations));
        when(merchantMemberInvitationService.checkInvitation(any(MerchantMemberInvitations.class))).thenReturn(Mono.just(invitations));
        when(userService.create(any(User.class))).thenReturn(Mono.just(user));
        when(verificationStatusService.unverifiedMerchant(any(User.class)))
                .thenReturn(Mono.just(verificationStatusForUser1.toBuilder()
                        .id(statusVerifyId)
                        .profileId(userId)
                        .build()));
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(profileHistory1.toBuilder()
                        .id(historyId)
                        .profileId(userId)
                        .profileType(ProfileType.MERCHANT_MEMBER)
                        .build()));
        when(merchantMemberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember.toBuilder()
                .merchantId(merchantId)
                .memberRole(MemberRole.MANAGER)
                .userId(userId)
                .build()));
        //then
        StepVerifier.create(merchantMemberService.createNewMerchantMember(invitationId))
                .expectNextMatches(merchantMember1 -> merchantMember1.getId().equals(merchantMember.getId()) &
                        merchantMember1.getMemberRole().equals(MemberRole.MANAGER) &
                        merchantMember1.getCreated().isBefore(LocalDateTime.now()))
                .expectComplete()
                .verify();
    }
}