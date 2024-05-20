package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.repository.VerificationStatusRepository;
import net.ilya.users_api_microservice_on_webflux.service.VerificationStatusService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationStatusServiceImpl implements VerificationStatusService {
    private final VerificationStatusRepository statusRepository;


    @Override
    public Mono<VerificationStatus> findById(UUID uuid) {
        log.info("IN VerificationStatusServiceImpl findById -{}",uuid);
        return statusRepository.findById(uuid);
    }

    @Override
    public Mono<VerificationStatus> create(VerificationStatus verificationStatus) {
        log.info("IN VerificationStatusServiceImpl create -{}",verificationStatus);
        return statusRepository.save(verificationStatus.toBuilder()
                .updated(LocalDateTime.now())
                .created(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<VerificationStatus> update(VerificationStatus verificationStatus) {
        log.info("IN VerificationStatusServiceImpl update -{}",verificationStatus);
        return statusRepository.findById(verificationStatus.getId())
                .map(verificationStatus1 -> verificationStatus1.toBuilder().build())
                .flatMap(statusRepository::save);
    }

    @Override
    public Mono<VerificationStatus> deleted(UUID uuid) {
        log.info("IN VerificationStatusServiceImpl deleted -{}",uuid);
        return statusRepository.deleteById(uuid).then(Mono.empty());
    }

    @Override
    public Flux<VerificationStatus> findAll() {
        log.info("IN VerificationStatusServiceImpl findAll");
        return statusRepository.findAll();
    }

    @Override
    public Mono<VerificationStatus> unverifiedIndividual(User user) {
        log.info("IN VerificationStatusServiceImpl unverifiedIndividual -{}",user);
        return statusRepository.save(
                VerificationStatus.builder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .details("unverified user" + user.getFirstName())
                        .profileId(user.getId())
                        .profileType(ProfileType.INDIVIDUAL)
                        .verificationStatus(VerificationEntityStatus.UNVERIFIED)
                        .build());
    }

    @Override
    public Mono<VerificationStatus> unverifiedMerchant(User user) {
        log.info("IN VerificationStatusServiceImpl unverifiedMerchant -{}",user);
        return statusRepository.save(
                VerificationStatus.builder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .details("unverified user" + user.getFirstName())
                        .profileId(user.getId())
                        .profileType(ProfileType.MERCHANT)
                        .verificationStatus(VerificationEntityStatus.UNVERIFIED)
                        .build());
    }

    @Override
    public Mono<VerificationStatus> unverifiedMerchantMember(User user) {
        log.info("IN VerificationStatusServiceImpl unverifiedMerchantMember -{}",user);
        return statusRepository.save(
                VerificationStatus.builder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .details("unverified user" + user.getFirstName())
                        .profileId(user.getId())
                        .profileType(ProfileType.MERCHANT_MEMBER)
                        .verificationStatus(VerificationEntityStatus.UNVERIFIED)
                        .build());
    }

    @Override
    public Mono<VerificationStatus> verifiedIndividual(Individual individual) {
        log.info("IN VerificationStatusServiceImpl verifiedIndividual -{}",individual);
        return statusRepository.findByProfileId(individual.getUserId())
                .map(verificationStatus -> verificationStatus.toBuilder()
                        .updated(LocalDateTime.now())
                        .verificationStatus(VerificationEntityStatus.VERIFIED)
                        .build())
                .flatMap(statusRepository::save);
    }

    @Override
    public Mono<VerificationStatus> verifiedMerchant(Merchant merchant) {
        log.info("IN VerificationStatusServiceImpl verifiedMerchant -{}",merchant);
        return statusRepository.findByProfileId(merchant.getCreatorId())
                .map(verificationStatus -> verificationStatus.toBuilder()
                        .updated(LocalDateTime.now())
                        .verificationStatus(VerificationEntityStatus.VERIFIED)
                        .build())
                .flatMap(statusRepository::save);
    }

    @Override
    public Mono<VerificationStatus> verifiedMerchantMember(MerchantMember merchantMember) {
        log.info("IN VerificationStatusServiceImpl verifiedMerchantMember -{}",merchantMember);
        return statusRepository.findByProfileId(merchantMember.getUserId())
                .map(verificationStatus -> verificationStatus.toBuilder()
                        .updated(LocalDateTime.now())
                        .verificationStatus(VerificationEntityStatus.VERIFIED)
                        .build())
                .flatMap(statusRepository::save);
    }
}
