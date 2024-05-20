package net.ilya.users_api_microservice_on_webflux.service.impl;

import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.repository.MerchantRepository;
import net.ilya.users_api_microservice_on_webflux.service.ProfileHistoryService;
import net.ilya.users_api_microservice_on_webflux.service.UserService;
import net.ilya.users_api_microservice_on_webflux.service.VerificationStatusService;
import net.ilya.users_api_microservice_on_webflux.util.DateUtilsService;
import net.ilya.users_api_microservice_on_webflux.utill.JsonParserCustom;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantServiceImplTest {
    @Mock
    private VerificationStatusService verificationStatusService;
    @Mock
    private ProfileHistoryServiceImpl profileHistoryService;
    @Mock
    private JsonParserCustom jsonParserCustom;
    @Mock
    private UserService userService;
    @Mock
    private MerchantRepository merchantRepository;
    @InjectMocks
    private MerchantServiceImpl merchantService;


    @Test
    void findById() {
        //given
        UUID saveId = UUID.randomUUID();
        Merchant merchantWithDate = DateUtilsService.getMerchantWithDate().toBuilder()
                .id(saveId)
                .status(StatusEntity.ACTIVE)
                .build();
        //when
        when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantWithDate));
        //then
        StepVerifier
                .create(merchantService.findById(saveId))
                .expectNextMatches(merchant -> merchant.getId().equals(merchantWithDate.getId()) &
                        merchant.getEmail().equals(merchantWithDate.getEmail()) &
                        merchant.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given
        UUID saveId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID historyId = UUID.randomUUID();
        UUID verifyId = UUID.randomUUID();

        User userWithDate = DateUtilsService.getUserWithDate().toBuilder()
                .build();
        Merchant merchant = DateUtilsService.getMerchantWithOutDate().toBuilder()
                .creator(userWithDate)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();
        VerificationStatus verificationStatus = DateUtilsService.getVerificationStatusForUser1();
        //when
        when(merchantRepository.findByCompanyNameAndEmailAndPhoneNumberAndCompanyId(anyString(), anyString(), anyString(), anyLong())).thenReturn(Mono.empty());
        when(userService.create(any(User.class))).thenReturn(Mono.just(userWithDate.toBuilder()
                .id(creatorId)
                .build()));
        when(jsonParserCustom.toJasonJacksonMerchant(any(Merchant.class))).thenReturn(merchant.toString());
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(historyId)
                .created(LocalDateTime.now())
                .profileId(creatorId)
                .reason(HistoryReason.CREATE.name())
                .profileType(ProfileType.MERCHANT)
                .changedValues(merchant.toString())
                .build()));
        when(verificationStatusService.unverifiedMerchant(any(User.class))).thenReturn(Mono.just(verificationStatus.toBuilder()
                .id(verifyId)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .profileId(creatorId)
                .profileType(ProfileType.MERCHANT)
                .build()));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(Mono.just(merchant.toBuilder()
                .creatorId(creatorId)
                .id(saveId)
                .status(StatusEntity.ACTIVE)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .filled(false)
                .build()));
        //then
        StepVerifier
                .create(merchantService.create(merchant))
                .expectNextMatches(merchantSave -> merchantSave.getId().equals(saveId) &
                        merchantSave.getCreatorId().equals(creatorId) &
                        merchantSave.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();

    }

    @Test
    void update() {
        //given
        UUID merchantExistId = UUID.randomUUID();
        UUID userExistId = UUID.randomUUID();
        User userWithDate = DateUtilsService.getUserWithDate().toBuilder()
                .id(userExistId)
                .build();
        Merchant merchantExist = DateUtilsService.getMerchantWithOutDate().toBuilder()
                .id(merchantExistId)
                .creator(userWithDate)
                .build();
        User user = DateUtilsService.getUserWithDate().toBuilder()
                .id(userExistId)
                .firstName("Update 1")
                .lastName("Update 2")
                .build();
        Merchant merchantToUpdate = DateUtilsService.getMerchantWithDate().toBuilder()
                .id(merchantExistId)
                .creator(user)
                .creatorId(user.getId())
                .status(StatusEntity.UPDATED)
                .updated(LocalDateTime.now())
                .phoneNumber("234523452")
                .companyName("TEST UPDATE")
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();
        VerificationStatus verificationStatus = DateUtilsService.getVerificationStatusForUser1();
        //when
        when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantExist));
        when(userService.update(any(User.class))).thenReturn(Mono.just(userWithDate.toBuilder()
                .updated(LocalDateTime.now())
                .status(StatusEntity.UPDATED)
                .firstName("Update 1")
                .lastName("Update 2")
                .build()));
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .profileId(userExistId)
                .reason(HistoryReason.UPDATE.name())
                .profileType(ProfileType.MERCHANT)
                .changedValues(merchantToUpdate.toString())
                .build()));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(Mono.just(merchantToUpdate.toBuilder()
                .id(merchantExistId)
                .creatorId(userExistId)
                .status(StatusEntity.UPDATED)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .filled(false)
                .build()));
        //then
        StepVerifier
                .create(merchantService.update(merchantToUpdate))
                .expectNextMatches(merchantUpdate -> merchantUpdate.getId().equals(merchantExistId) &
                        merchantUpdate.getCreatorId().equals(userExistId) &
                        merchantUpdate.getStatus().equals(StatusEntity.UPDATED) &
                        merchantUpdate.getCompanyName().equalsIgnoreCase("TEST UPDATE"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleted() {
        //given
        UUID merchantID = UUID.randomUUID();
        UUID userID = UUID.randomUUID();
        UUID profileHistorySave = UUID.randomUUID();
        User userWithDate = DateUtilsService.getUserWithDate().toBuilder()
                .id(userID)
                .status(StatusEntity.ACTIVE)
                .build();
        Merchant merchant = DateUtilsService.getMerchantWithOutDate().toBuilder()
                .id(merchantID)
                .creatorId(userID)
                .status(StatusEntity.ACTIVE)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();
        //when
        when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchant));
        when(userService.deleted(any(UUID.class))).thenReturn(Mono.just(userWithDate.toBuilder()
                .status(StatusEntity.DELETED)
                .archivedAt(LocalDateTime.now())
                .build()));
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(userWithDate.getId())
                .created(LocalDateTime.now())
                .profileId(userWithDate.getId())
                .reason(HistoryReason.DELETED.name())
                .profileType(ProfileType.MERCHANT)
                .changedValues(merchant.toString())
                .build()));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(Mono.just(merchant.toBuilder()
                .status(StatusEntity.DELETED)
                .archivedAt(LocalDateTime.now())
                .build()));
        //then
        StepVerifier.create(merchantService.deleted(merchantID))
                .expectNextMatches(merchant1 -> merchant1.getId().equals(merchant.getId()) &
                        merchant1.getStatus().equals(StatusEntity.DELETED) &
                        merchant1.getArchivedAt().isBefore(LocalDateTime.now()))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        Merchant merchant1 = DateUtilsService.getMerchantWithDate().toBuilder()
                .id(UUID.randomUUID())
                .build();
        Merchant merchant2 = DateUtilsService.getMerchantWithDate().toBuilder()
                .id(UUID.randomUUID())
                .build();
        //when
        when(merchantRepository.findAll()).thenReturn(Flux.just(merchant1, merchant2));
        //then
        StepVerifier.create(merchantService.findAll())
                .expectNext(merchant1)
                .expectNext(merchant2)
                .expectComplete()
                .verify();
    }

    @Test
    void verifiedMerchant() {
        //given
        UUID userId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        UUID historyId = UUID.randomUUID();
        UUID verifyId = UUID.randomUUID();

        User userWithDate = DateUtilsService.getUserWithDate().toBuilder()
                .id(userId)
                .build();
        Merchant merchant = DateUtilsService.getMerchantWithOutDate().toBuilder()
                .id(merchantId)
                .creatorId(userId)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1();
        VerificationStatus verificationStatus = DateUtilsService.getVerificationStatusForUser1();
        //when
        when(verificationStatusService.verifiedMerchant(any(Merchant.class))).thenReturn(Mono.just(verificationStatus.toBuilder()
                .id(verifyId)
                .profileId(userId)
                .profileType(ProfileType.MERCHANT)
                .verificationStatus(VerificationEntityStatus.VERIFIED)
                .updated(LocalDateTime.now())
                .build()));
        when(userService.verified(any(VerificationStatus.class))).thenReturn(Mono.just(userWithDate.toBuilder()
                .verifiedAt(LocalDateTime.now())
                .status(StatusEntity.UPDATED)
                .build()));
        when(jsonParserCustom.toJasonJacksonMerchant(any(Merchant.class))).thenReturn(merchant.toString());
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(historyId)
                .created(LocalDateTime.now())
                .profileId(userWithDate.getId())
                .reason(HistoryReason.VERIFIED.name())
                .profileType(ProfileType.MERCHANT)
                .changedValues(merchant.toString())
                .build()));
        when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchant));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(Mono.just(merchant.toBuilder()
                .status(StatusEntity.UPDATED)
                .verifiedAt(LocalDateTime.now())
                .build()));
        //then
        StepVerifier.create(merchantService.verifiedMerchant(merchant))
                .expectNextMatches(merchant1 -> merchant1.getId().equals(merchantId) &
                        merchant1.getStatus().equals(StatusEntity.UPDATED) &
                        merchant1.getVerifiedAt().isBefore(LocalDateTime.now()))
                .expectComplete()
                .verify();
    }
}