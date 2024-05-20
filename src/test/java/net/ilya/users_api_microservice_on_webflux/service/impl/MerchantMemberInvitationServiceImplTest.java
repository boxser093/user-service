package net.ilya.users_api_microservice_on_webflux.service.impl;

import net.ilya.users_api_microservice_on_webflux.entity.MerchantMemberInvitations;
import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;
import net.ilya.users_api_microservice_on_webflux.repository.MerchantMemberInvitationRepository;
import net.ilya.users_api_microservice_on_webflux.repository.MerchantMemberRepository;
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
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantMemberInvitationServiceImplTest {
    @Mock
    private MerchantMemberInvitationRepository merchantMemberInvitationRepository;
    @InjectMocks
    private MerchantMemberInvitationServiceImpl memberInvitationService;

    @Test
    void findById() {
        //given
        UUID randomId = DateUtilsService.getRandomId();
        MerchantMemberInvitations invitations = DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .id(randomId)
                .build();
        //when
        when(merchantMemberInvitationRepository.findById(any(UUID.class))).thenReturn(Mono.just(invitations));
        //then
        StepVerifier.create(memberInvitationService.findById(randomId))
                .expectNextMatches(invitationsExist -> invitationsExist.getId().equals(randomId) &
                        invitationsExist.getFirstName().equals(invitations.getFirstName()) &
                        invitationsExist.getLastName().equals(invitations.getLastName()))
                .expectComplete()
                .verify();

    }

    @Test
    void create() {
        //given
        UUID idBeforeSave = DateUtilsService.getRandomId();
        LocalDateTime checkDateCreate = LocalDateTime.of(2023, 12, 10, 20, 10, 29);
        MerchantMemberInvitations merchantMemberInvitationCreate = DateUtilsService.getMerchantMemberInvitationCreate().toBuilder()
                .id(idBeforeSave)
                .status(StatusEntity.ACTIVE)
                .created(LocalDateTime.now())
                .build();
        //when
        when(merchantMemberInvitationRepository.save(any(MerchantMemberInvitations.class))).thenReturn(Mono.just(merchantMemberInvitationCreate));
        //then
        StepVerifier.create(memberInvitationService.create(merchantMemberInvitationCreate))
                .expectNextMatches(save -> save.getStatus().equals(StatusEntity.ACTIVE) &
                        save.getId().equals(idBeforeSave))
                .expectComplete()
                .verify();

    }

    @Test
    void update() {
        //given
        UUID randomId = DateUtilsService.getRandomId();
        MerchantMemberInvitations invitations = DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .id(randomId)
                .build();

        MerchantMemberInvitations forUpdate = DateUtilsService.getMerchantMemberInvitationCreate().toBuilder()
                .id(randomId)
                .firstName("Update 1")
                .lastName("Update 2")
                .status(StatusEntity.UPDATED)
                .created(LocalDateTime.now())
                .build();
        //when
        when(merchantMemberInvitationRepository.findById(any(UUID.class))).thenReturn(Mono.just(invitations));
        when(merchantMemberInvitationRepository.save(any(MerchantMemberInvitations.class))).thenReturn(Mono.just(forUpdate));
        //then
        StepVerifier.create(memberInvitationService.update(forUpdate))
                .expectNextMatches(update -> update.getStatus().equals(StatusEntity.UPDATED) &
                        update.getId().equals(randomId) &
                        update.getFirstName().equals("Update 1") &
                        update.getLastName().equals("Update 2"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleted() {
        //given
        UUID randomId = DateUtilsService.getRandomId();
        MerchantMemberInvitations invitations = DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .id(randomId)
                .build();

        //when
        when(merchantMemberInvitationRepository.findById(any(UUID.class))).thenReturn(Mono.just(invitations));
        when(merchantMemberInvitationRepository.save(any(MerchantMemberInvitations.class))).thenReturn(Mono.just(invitations.toBuilder()
                .status(StatusEntity.DELETED)
                .build()));
        //then
        StepVerifier.create(memberInvitationService.deleted(randomId))
                .expectNextMatches(update -> update.getStatus().equals(StatusEntity.DELETED) &
                        update.getId().equals(randomId))
                .expectComplete()
                .verify();

    }

    @Test
    void findAll() {
        //given
        UUID randomId = DateUtilsService.getRandomId();
        MerchantMemberInvitations invitations = DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .id(randomId)
                .build();
        UUID randomId2 = DateUtilsService.getRandomId();
        MerchantMemberInvitations invitations1 = DateUtilsService.getMerchantMemberInvitationCreate().toBuilder()
                .id(randomId2)
                .firstName("First 1")
                .lastName("First 2")
                .status(StatusEntity.UPDATED)
                .created(LocalDateTime.now())
                .build();
        //when
        when(merchantMemberInvitationRepository.findAll()).thenReturn(Flux.just(invitations, invitations1));
        //then
        StepVerifier.create(memberInvitationService.findAll())
                .expectNext(invitations)
                .expectNext(invitations1)
                .expectComplete()
                .verify();

    }

    @Test
    void checkInvitation() {
        //given
        UUID randomId = DateUtilsService.getRandomId();
        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 12, 10, 30, 30, 30);
        MerchantMemberInvitations invitations = DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .id(randomId)
                .created(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .expires(localDateTime)
                .build();
        //then
        StepVerifier
                .create(memberInvitationService.checkInvitation(invitations))
                .expectNextMatches(verify -> verify.getId().equals(invitations.getId()))
                .expectComplete()
                .verify();

    }
}