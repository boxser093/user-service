package net.ilya.users_api_microservice_on_webflux.service.impl;


import net.ilya.users_api_microservice_on_webflux.dto.*;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.mapper.IndividualMapper;
import net.ilya.users_api_microservice_on_webflux.repository.IndividualRepository;
import net.ilya.users_api_microservice_on_webflux.service.*;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividualServiceImplTest {
    @Mock
    private IndividualMapper mapper;
    @Mock
    private IndividualRepository individualRepository;
    @Mock
    private CountryService countryService;
    @Mock
    private AddressService addressService;
    @Mock
    private UserService userService;
    @Mock
    private ProfileHistoryService profileHistoryService;
    @Mock
    private VerificationStatusService verificationStatusService;
    @Mock
    private JsonParserCustom jsonParser;
    @InjectMocks
    private IndividualServiceImpl individualService;

    @Test
    void findById() {
        //given
        UUID uuid = UUID.randomUUID();
        Individual individual1 = DateUtilsService.getIndividual1().toBuilder()
                .id(uuid)
                .build();
        //when
        when(individualRepository.findById(uuid)).thenReturn(Mono.just(individual1));
        //then
        StepVerifier.
                create(individualService.findById(uuid))
                .expectNextMatches(individual -> individual.getId().equals(individual1.getId())
                        && individual.getPhoneNumber().equals(individual1.getPhoneNumber()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given
        Country country = DateUtilsService.getCountryWithoutDate();
        Address address = DateUtilsService.getAddressWithOutDate().toBuilder()
                .country(country)
                .build();
        User user = DateUtilsService.getUserWithOutDate().toBuilder()
                .address(address)
                .build();
        Individual individual = DateUtilsService.getIndividualWithOutDate().toBuilder()
                .userData(user)
                .build();
        UUID userId = UUID.randomUUID();
        UUID individualId = UUID.randomUUID();
        //when
        when(individualRepository.findByEmailAndPassportNumberAndPhoneNumber(anyString(), anyString(), anyString())).thenReturn(Mono.empty());
        when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individual.toBuilder()
                .id(individualId)
                .userId(userId)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .build()));
        //then
        StepVerifier.create(individualService.create(individual))
                .expectNextMatches(individual1 -> individual1.getId().equals(individualId) &
                        individual1.getUserId().equals(userId) &
                        individual1.getPassportNumber().equals(individual.getPassportNumber()) &
                        individual1.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given

        //when

        //then
    }

    @Test
    void delete() {
        UUID uuid = UUID.randomUUID();
        Individual individual1 = DateUtilsService.getIndividual1().toBuilder()
                .id(uuid)
                .build();
        //when
        when(individualRepository.findById(uuid)).thenReturn(Mono.just(individual1));
        when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individual1.toBuilder()
                .status(StatusEntity.DELETED)
                .build()));
        //then
        StepVerifier.
                create(individualService.deleted(uuid))
                .expectNextMatches(individual -> individual.getId().equals(individual1.getId())
                        && individual.getStatus().equals(StatusEntity.DELETED))
                .expectComplete()
                .verify();

    }

    @Test
    void findAll() {
        //given
        Individual individual1 = DateUtilsService.getIndividual1();
        //when
        when(individualRepository.findAll()).thenReturn(Flux.just(individual1));
        //then
        StepVerifier
                .create(individualService.findAll())
                .expectNext(individual1)
                .expectComplete()
                .verify();
    }

    @Test
    void saveIndividual() {
        //given
        LocalDateTime createdEntityTime = LocalDateTime.now();
        LocalDateTime unverifiedTime = LocalDateTime.now();
        UUID addressId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID individualId = UUID.randomUUID();
        UUID profileHistoryId = UUID.randomUUID();
        UUID verificationStatusId = UUID.randomUUID();
        Country country1 = DateUtilsService.getCountry1();
        CountryDto countryDto = DateUtilsService.getCountryDto1();
        Address address1 = DateUtilsService.getAddress1().toBuilder()
                .country(country1)
                .build();
        AddressDto addressDto = DateUtilsService.getAddressDto1().toBuilder()
                .country(countryDto)
                .build();
        User user1 = DateUtilsService.getUser1().toBuilder()
                .address(address1)
                .build();
        UserDto userDto = DateUtilsService.getUserDto1().toBuilder()
                .address(addressDto)
                .build();
        VerificationStatus verificationStatusForUser1 = DateUtilsService.getVerificationStatusForUser1().toBuilder()
                .profileId(userId)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1().toBuilder()
                .id(profileHistoryId)
                .profileId(userId)
                .build();
        Individual individual1 = DateUtilsService.getIndividual1().toBuilder()
                .userData(user1)
                .build();
        IndividualDto individualDto = DateUtilsService.getIndividualDto1().toBuilder()
                .userData(userDto)
                .build();
        //when
        when(mapper.map(any(IndividualDto.class))).thenReturn(individual1);
        when(countryService.create(any(Country.class))).thenReturn(Mono.just(country1.toBuilder()
                .id(1L)
                .created(createdEntityTime)
                .updated(createdEntityTime)
                .status(StatusEntity.ACTIVE)
                .build()));
        when(addressService.create(any(Address.class))).thenReturn(Mono.just(address1.toBuilder()
                .countryId(1L)
                .id(addressId)
                .created(createdEntityTime)
                .updated(createdEntityTime)
                .archived(createdEntityTime)
                .build()));
        when(userService.create(any(User.class))).thenReturn(Mono.just(user1.toBuilder()
                .addressId(addressId)
                .created(createdEntityTime)
                .updated(createdEntityTime)
                .archivedAt(createdEntityTime)
                .verifiedAt(createdEntityTime)
                .status(StatusEntity.ACTIVE)
                .filled(false)
                .build()));
        when(verificationStatusService.unverifiedIndividual(any(User.class))).thenReturn(Mono.just(verificationStatusForUser1.toBuilder()
                .id(verificationStatusId)
                .created(unverifiedTime)
                .profileId(userId)
                .build()));
        when(jsonParser.toJasonJacksonIndividual(any(IndividualDto.class))).thenReturn("Changed");
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(UUID.randomUUID())
                .profileId(userId)
                .build()));
        when(individualRepository.findByEmailAndPassportNumberAndPhoneNumber(anyString(), anyString(), anyString())).thenReturn(Mono.empty());
        when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individual1.toBuilder()
                .userId(userId)
                .verifiedAt(unverifiedTime)
                .build()));
        IndividualDto individualDto2 = IndividualDto.builder()
                .id(individualId)
                .phoneNumber("+4358999875")
                .passportNumber("1234456789")
                .email("foo@google.com")
                .created(createdEntityTime)
                .updated(createdEntityTime)
                .archivedAt(createdEntityTime)
                .verifiedAt(createdEntityTime)
                .status(StatusEntity.ACTIVE)
                .userId(userId)
                .verifiedAt(unverifiedTime)
                .build();
        when(mapper.map(any(Individual.class))).thenReturn(individualDto2);
        //then
        StepVerifier.create(individualService.saveIndividual(individualDto))
                .expectNextMatches(individualDto1 -> individualDto1.getId().equals(individualDto2.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    void deletedIndividual() {
        //given
        UUID individualId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime timeDeleted = LocalDateTime.now();
        User user = DateUtilsService.getUserWithDate().toBuilder()
                .id(userId)
                .build();
        Individual individual1 = DateUtilsService.getIndividualWithDate().toBuilder()
                .id(individualId)
                .userId(userId)
                .build();
        Individual individualDeleted = DateUtilsService.getIndividualWithDate().toBuilder()
                .id(individualId)
                .userId(userId)
                .status(StatusEntity.DELETED)
                .archivedAt(timeDeleted)
                .updated(timeDeleted)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1().toBuilder()
                .id(UUID.randomUUID())
                .profileId(userId)
                .build();
        IndividualDto individualDto = IndividualDto.builder()
                .id(individualId)
                .userId(userId)
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("test@foo.com")
                .status(StatusEntity.DELETED)
                .archivedAt(timeDeleted)
                .updated(timeDeleted)
                .build();
        //when
        when(individualRepository.findById(any(UUID.class))).thenReturn(Mono.just(individual1));
        when(userService.deleted(any(UUID.class))).thenReturn(Mono.just(user));
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(UUID.randomUUID())
                .profileId(userId)
                .build()));
        when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individualDeleted));
        when(mapper.map(any(Individual.class))).thenReturn(individualDto);
        //then
        StepVerifier
                .create(individualService.deletedIndividual(individualId))
                .expectNextMatches(individualDto1 -> individualDto1.getId().equals(individualId) &
                        individualDto1.getStatus().equals(StatusEntity.DELETED))
                .expectComplete()
                .verify();
    }

    @Test
    void updateIndividual() {
        //given
        UUID userId = UUID.randomUUID();
        UUID individualId = UUID.randomUUID();
        LocalDateTime timeUpdate = LocalDateTime.now();
        Country country = DateUtilsService.getCountryWithoutDate();
        Address address = DateUtilsService.getAddressWithOutDate().toBuilder()
                .country(country)
                .build();
        User user = DateUtilsService.getUserWithDate().toBuilder()
                .id(userId)
                .address(address)
                .build();
        Individual individual1 = DateUtilsService.getIndividualWithDate().toBuilder()
                .id(individualId)
                .userId(userId)
                .userData(user)
                .build();
        VerificationStatus verificationStatusForUser1 = DateUtilsService.getVerificationStatusForUser1().toBuilder()
                .profileId(userId)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1().toBuilder()
                .id(UUID.randomUUID())
                .profileId(userId)
                .build();
        Individual individualUpdated = DateUtilsService.getIndividualWithDate().toBuilder()
                .id(individualId)
                .userId(userId)
                .status(StatusEntity.UPDATED)
                .archivedAt(timeUpdate)
                .updated(timeUpdate)
                .build();
        IndividualDto individualDto = IndividualDto.builder()
                .id(individualId)
                .userId(userId)
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("test@foo.com")
                .status(StatusEntity.UPDATED)
                .archivedAt(timeUpdate)
                .updated(timeUpdate)
                .build();
        IndividualDto individualDto2 = IndividualDto.builder().build();
        //when
        when(mapper.map(any(IndividualDto.class))).thenReturn(individual1);
        when(individualRepository.findById(any(UUID.class))).thenReturn(Mono.just(individual1));
        when(userService.update(any(User.class))).thenReturn(Mono.just(user));
        when(jsonParser.toJasonJacksonIndividual(any(IndividualDto.class))).thenReturn("Changed");
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(UUID.randomUUID())
                .profileId(userId)
                .reason(HistoryReason.UPDATE.name())
                .build()));
        when(addressService.update(any(Address.class))).thenReturn(Mono.just(mock(Address.class)));
        when(countryService.update(any(Country.class))).thenReturn(Mono.just(mock(Country.class)));
        when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individualUpdated));
        when(mapper.map(any(Individual.class))).thenReturn(individualDto);
        //then
        StepVerifier
                .create(individualService.updateIndividual(individualDto2))
                .expectNextMatches(individualDto1 -> individualDto1.getId().equals(individualId) &
                        individualDto1.getStatus().equals(StatusEntity.UPDATED))
                .expectComplete()
                .verify();

    }

    @Test
    void verified() {
        //give
        UUID userId = UUID.randomUUID();
        UUID individualId = UUID.randomUUID();
        LocalDateTime timeUpdate = LocalDateTime.now();
        LocalDateTime verifyTime = LocalDateTime.now();
        Country country = DateUtilsService.getCountryWithoutDate();
        Address address = DateUtilsService.getAddressWithOutDate().toBuilder()
                .country(country)
                .build();
        User user = DateUtilsService.getUserWithDate().toBuilder()
                .id(userId)
                .address(address)
                .build();
        Individual individual1 = DateUtilsService.getIndividualWithDate().toBuilder()
                .id(individualId)
                .userId(userId)
                .userData(user)
                .build();
        VerificationStatus verificationStatusForUser1 = DateUtilsService.getVerificationStatusForUser1().toBuilder()
                .profileId(userId)
                .build();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1().toBuilder()
                .id(UUID.randomUUID())
                .profileId(userId)
                .build();
        Individual individualUpdated = DateUtilsService.getIndividualWithDate().toBuilder()
                .id(individualId)
                .userId(userId)
                .status(StatusEntity.UPDATED)
                .archivedAt(timeUpdate)
                .updated(timeUpdate)
                .build();
        IndividualDto individualDto = IndividualDto.builder()
                .id(individualId)
                .userId(userId)
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("test@foo.com")
                .status(StatusEntity.UPDATED)
                .archivedAt(timeUpdate)
                .updated(verifyTime)
                .verifiedAt(verifyTime)
                .build();
        IndividualDto individualDto2 = IndividualDto.builder().build();
        //when
        when(mapper.map(any(IndividualDto.class))).thenReturn(individual1);
        when(verificationStatusService.verifiedIndividual(any(Individual.class))).thenReturn(Mono.just(verificationStatusForUser1.toBuilder()
                .verificationStatus(VerificationEntityStatus.VERIFIED)
                .updated(verifyTime)
                .build()));
        when(userService.verified(any(VerificationStatus.class))).thenReturn(Mono.just(user.toBuilder()
                .updated(verifyTime)
                .build()));
        when(jsonParser.toJasonJacksonIndividual(any(IndividualDto.class))).thenReturn("Changed");
        when(profileHistoryService.create(any(UUID.class), any(ProfileType.class), anyString(), anyString(), anyString())).thenReturn(Mono.just(profileHistory1.toBuilder()
                .id(UUID.randomUUID())
                .profileId(userId)
                .reason(HistoryReason.VERIFIED.name())
                .build()));
        when(individualRepository.findIndividualByUserId(any(UUID.class))).thenReturn(Mono.just(individual1));
        when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individual1.toBuilder()
                .updated(verifyTime)
                .verifiedAt(verifyTime)
                .build()));
        when(mapper.map(any(Individual.class))).thenReturn(individualDto);
        //then
        StepVerifier.create(individualService.verified(individualDto2))
                .expectNextMatches(individualDto1 -> individualDto1.getId().equals(individualId) &
                        individualDto1.getVerifiedAt().isEqual(verifyTime))
                .expectComplete()
                .verify();
    }

    @Test
    void updateVerifiedDate() {
        //give
        UUID individualId = UUID.randomUUID();
        LocalDateTime verifyTime = LocalDateTime.now();
        ProfileHistory profileHistory1 = DateUtilsService.getProfileHistory1().toBuilder()
                .id(UUID.randomUUID())
                .profileId(UUID.randomUUID())
                .created(verifyTime)
                .build();
        Individual individual1 = DateUtilsService.getIndividualWithDate().toBuilder()
                .id(individualId)
                .build();
        //when
        when(individualRepository.findIndividualByUserId(any(UUID.class))).thenReturn(Mono.just(individual1));
        when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individual1.toBuilder()
                .verifiedAt(verifyTime)
                .status(StatusEntity.UPDATED)
                .updated(verifyTime)
                .build()));
        //then
        StepVerifier.create(individualService.updateVerifiedDate(profileHistory1))
                .expectNextMatches(individual -> individual.getId().equals(individualId) &
                        individual.getVerifiedAt().equals(verifyTime) &
                        individual.getStatus().equals(StatusEntity.UPDATED))
                .expectComplete()
                .verify();
    }
}