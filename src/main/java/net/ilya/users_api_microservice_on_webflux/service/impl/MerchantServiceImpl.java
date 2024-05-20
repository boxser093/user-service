package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.dto.MerchantDto;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.error.DuplicateResourceException;
import net.ilya.users_api_microservice_on_webflux.error.IndividualNonExistentById;
import net.ilya.users_api_microservice_on_webflux.error.ObjectNotExist;
import net.ilya.users_api_microservice_on_webflux.mapper.MerchantMapper;
import net.ilya.users_api_microservice_on_webflux.repository.MerchantRepository;
import net.ilya.users_api_microservice_on_webflux.service.MerchantService;
import net.ilya.users_api_microservice_on_webflux.service.UserService;
import net.ilya.users_api_microservice_on_webflux.service.VerificationStatusService;
import net.ilya.users_api_microservice_on_webflux.utill.JsonParserCustom;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    private final VerificationStatusService verificationStatusService;
    private final ProfileHistoryServiceImpl profileHistoryService;
    private final JsonParserCustom jsonParserCustom;
    private final UserService userService;
    private final MerchantRepository merchantRepository;

    @Override
    public Mono<Merchant> findById(UUID uuid) {
        log.info("# In MerchantServiceImpl findById -{}", uuid);
        return merchantRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())));
    }

    @Override
    public Mono<Merchant> create(Merchant merchant) {
        log.info("# In MerchantServiceImpl create -{}", merchant);
        return merchantRepository.findByCompanyNameAndEmailAndPhoneNumberAndCompanyId(merchant.getCompanyName(), merchant.getEmail(), merchant.getPhoneNumber(), merchant.getCompanyId())
                .flatMap(merchantExist -> Mono.error(new DuplicateResourceException(String.format("%s,%s,%s,%d", merchant.getCompanyName(), merchant.getEmail(), merchant.getPhoneNumber(), merchant.getCompanyId()))))
                .switchIfEmpty(Mono.defer(() -> userService.create(merchant.getCreator())
                        .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.MERCHANT, HistoryReason.CREATE.name(), "New merchant", jsonParserCustom.toJasonJacksonMerchant(merchant))
                                .flatMap(profileHistory -> verificationStatusService.unverifiedMerchant(user))
                                .flatMap(verificationStatus -> merchantRepository.save(merchant.toBuilder()
                                        .creatorId(verificationStatus.getProfileId())
                                        .created(LocalDateTime.now())
                                        .updated(LocalDateTime.now())
                                        .verifiedAt(LocalDateTime.now())
                                        .archivedAt(LocalDateTime.now())
                                        .status(StatusEntity.ACTIVE)
                                        .filled(false)
                                        .build()))))
                ).cast(Merchant.class);
    }


    @Override
    public Mono<Merchant> update(Merchant merchant) {
        log.info("# In MerchantServiceImpl update -{}", merchant);
        return merchantRepository.findById(merchant.getId())
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(merchant.getId().toString())))
                .flatMap(merchant1 -> userService.update(merchant1.getCreator())
                        .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.MERCHANT, HistoryReason.UPDATE.name(), String.format("Update new merchant %s, %s, %s", merchant.getEmail(), merchant.getPhoneNumber(), merchant.getCompanyName()), merchant.getCreated().toString())
                                .flatMap(profileHistory -> merchantRepository.save(merchant1.toBuilder()
                                        .email(merchant.getEmail())
                                        .phoneNumber(merchant.getPhoneNumber())
                                        .companyName(merchant.getCompanyName())
                                        .companyId(merchant.getCompanyId())
                                        .updated(LocalDateTime.now())
                                        .status(StatusEntity.UPDATED)
                                        .build()))));
    }

    @Override
    public Mono<Merchant> deleted(UUID uuid) {
        log.info("# In MerchantServiceImpl deleted -{}", uuid);
        return merchantRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new ObjectNotExist(uuid.toString())))
                .flatMap(merchant -> userService.deleted(merchant.getCreatorId())
                        .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.MERCHANT, HistoryReason.DELETED.name(), String.format("Delete user and merchant by id:%s", uuid), uuid.toString())
                                .flatMap(profileHistory -> merchantRepository.save(merchant.toBuilder()
                                        .updated(LocalDateTime.now())
                                        .archivedAt(LocalDateTime.now())
                                        .status(StatusEntity.DELETED)
                                        .build()))));
    }

    @Override
    public Flux<Merchant> findAll() {
        return merchantRepository.findAll();
    }

    @Override
    public Mono<Merchant> verifiedMerchant(Merchant merchant) {
        log.info("# In IndividualServiceImpl verifiedMerchant -{}", merchant);
        return verificationStatusService.verifiedMerchant(merchant)
                .flatMap(userService::verified)
                .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.MERCHANT, HistoryReason.VERIFIED.name(),
                                String.format("Verify merchant %s, %s, %s", merchant.getEmail(), merchant.getCompanyName(), merchant.getPhoneNumber()),
                                jsonParserCustom.toJasonJacksonMerchant(merchant))
                        .flatMap(profileHistory -> merchantRepository.findById(merchant.getId())
                                .map(merchant1 -> merchant1.toBuilder()
                                        .status(StatusEntity.UPDATED)
                                        .verifiedAt(LocalDateTime.now())
                                        .build())
                                .flatMap(merchantRepository::save)));
    }
}
