package net.ilya.users_api_microservice_on_webflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.ilya.users_api_microservice_on_webflux.dto.ErrorResponse;
import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.error.DuplicateResourceException;
import net.ilya.users_api_microservice_on_webflux.error.IndividualNonExistentById;
import net.ilya.users_api_microservice_on_webflux.mapper.IndividualMapper;
import net.ilya.users_api_microservice_on_webflux.repository.*;
import net.ilya.users_api_microservice_on_webflux.service.*;
import net.ilya.users_api_microservice_on_webflux.utill.JsonParserCustom;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndividualServiceImpl implements IndividualService {
    private final IndividualRepository individualRepository;
    private final IndividualMapper mapper;
    private final CountryService countryService;
    private final AddressService addressService;
    private final UserService userService;
    private final ProfileHistoryService profileHistoryService;
    private final VerificationStatusService verificationStatusService;
    private final JsonParserCustom jsonParser;

    @Override
    public Mono<Individual> findById(UUID uuid) {
        log.info("# In IndividualServiceImpl findById -{}", uuid);
        return individualRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new IndividualNonExistentById(uuid)));
    }

    @Override
    public Mono<Individual> create(Individual individual) {
        log.info("# In IndividualServiceImpl create -{}", individual);
        return individualRepository.findByEmailAndPassportNumberAndPhoneNumber(individual.getEmail(), individual.getPassportNumber(), individual.getPhoneNumber())
                .flatMap(individualExist -> Mono.error(new DuplicateResourceException(String.format("%s,%s,%s", individual.getEmail(), individual.getPassportNumber(), individual.getPhoneNumber()))))
                .switchIfEmpty(Mono.defer(() -> individualRepository.save(individual.toBuilder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .archivedAt(LocalDateTime.now())
                        .status(StatusEntity.ACTIVE)
                        .build()))).cast(Individual.class);

    }

    @Override
    public Mono<Individual> update(Individual individual) {
        log.info("# In IndividualServiceImpl update -{}", individual);
        return individualRepository.findById(individual.getId())
                .map(individual1 -> individual1.toBuilder()
                        .updated(LocalDateTime.now())
                        .passportNumber(individual.getPassportNumber())
                        .phoneNumber(individual.getPhoneNumber())
                        .email(individual.getEmail())
                        .status(StatusEntity.UPDATED)
                        .build())
                .flatMap(individualRepository::save);
    }

    @Override
    public Mono<Individual> deleted(UUID uuid) {
        log.info("# In IndividualServiceImpl deleted -{}", uuid);
        return individualRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new IndividualNonExistentById(uuid)))
                .map(individual -> individual.toBuilder()
                        .archivedAt(LocalDateTime.now())
                        .status(StatusEntity.DELETED)
                        .build())
                .flatMap(individualRepository::save);
    }

    @Override
    public Flux<Individual> findAll() {
        log.info("# In IndividualServiceImpl findAll ");
        return individualRepository.findAll();
    }

    @Override
    public Mono<IndividualDto> saveIndividual(IndividualDto individualDto) {
        Individual individual = mapper.map(individualDto);
        User user = individual.getUserData();
        Address address = user.getAddress();
        Country country = address.getCountry();
        log.info("# In IndividualServiceImpl saveIndividual -{}", individual);
        return countryService.create(country)
                .flatMap(country1 -> addressService.create(address.toBuilder()
                        .countryId(country1.getId())
                        .build()))
                .flatMap(address1 -> userService.create(user.toBuilder()
                        .addressId(address1.getId())
                        .build()))
                .flatMap(verificationStatusService::unverifiedIndividual)
                .flatMap(verificationStatus -> profileHistoryService.create(verificationStatus.getProfileId(), ProfileType.INDIVIDUAL, HistoryReason.CREATE.name(), String.format("Create new individual %s, %s, %s", individual.getEmail(), individual.getPassportNumber(), individual.getPassportNumber()), jsonParser.toJasonJacksonIndividual(individualDto)))
                .flatMap(profileHistory -> this.create(individual.toBuilder()
                        .userId(profileHistory.getProfileId())
                        .verifiedAt(profileHistory.getCreated())
                        .build())).map(mapper::map);
    }


    @Override
    public Mono<IndividualDto> deletedIndividual(UUID uuid) {
        log.info("# In IndividualServiceImpl saveIndividual -{}", uuid);
        return individualRepository.findById(uuid)
                .switchIfEmpty(Mono.error(() -> new IndividualNonExistentById(uuid)))
                .flatMap(individual -> userService.deleted(individual.getUserId())
                        .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.INDIVIDUAL, HistoryReason.DELETED.name(), String.format("Delete user and individual by id:%s", uuid), uuid.toString())
                                .flatMap(profileHistory -> this.deleted(individual.getId()).map(mapper::map))));
    }

    @Override
    public Mono<IndividualDto> updateIndividual(IndividualDto individualDto) {
        Individual individual = mapper.map(individualDto);
        log.info("# In IndividualServiceImpl updateIndividual -{}", individual);
        return individualRepository.findById(individual.getId())
                .switchIfEmpty(Mono.error(() -> new IndividualNonExistentById(individual.getId())))
                .flatMap(individual1 -> userService.update(individual.getUserData())
                        .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.INDIVIDUAL, HistoryReason.UPDATE.name(), String.format("Update new individual %s, %s, %s", individual.getEmail(), individual.getPassportNumber(), individual.getPassportNumber()), jsonParser.toJasonJacksonIndividual(individualDto))
                                .flatMap(profileHistory -> addressService.update(individual.getUserData().getAddress())
                                        .flatMap(address -> countryService.update(individual.getUserData().getAddress().getCountry())
                                                .flatMap(country -> this.update(individual)).map(mapper::map)))));

    }

    @Override
    public Mono<IndividualDto> verified(IndividualDto individualDto) {
        Individual individual = mapper.map(individualDto);
        log.info("# In IndividualServiceImpl verified -{}", individual);
        return verificationStatusService.verifiedIndividual(individual)
                .flatMap(userService::verified).log()
                .flatMap(user -> profileHistoryService.create(user.getId(), ProfileType.INDIVIDUAL, HistoryReason.VERIFIED.name(),
                                String.format("Update new individual %s, %s, %s", individual.getEmail(), individual.getPassportNumber(), individual.getPassportNumber()),
                                jsonParser.toJasonJacksonIndividual(individualDto))
                        .flatMap(this::updateVerifiedDate).map(mapper::map));
    }

    @Override
    public Mono<Individual> updateVerifiedDate(ProfileHistory profileHistory) {
        log.info("# In IndividualServiceImpl updateVerifiedDate -{}", profileHistory);
        return individualRepository.findIndividualByUserId(profileHistory.getProfileId())
                .map(individual1 -> individual1.toBuilder()
                        .verifiedAt(profileHistory.getCreated())
                        .updated(LocalDateTime.now())
                        .status(StatusEntity.UPDATED)
                        .build())
                .flatMap(individualRepository::save);
    }
}
