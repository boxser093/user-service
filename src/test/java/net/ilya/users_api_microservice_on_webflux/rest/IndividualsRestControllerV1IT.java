package net.ilya.users_api_microservice_on_webflux.rest;

import net.ilya.users_api_microservice_on_webflux.config.PostgresTestContainerConfig;
import net.ilya.users_api_microservice_on_webflux.dto.ErrorResponse;
import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.error.*;
import net.ilya.users_api_microservice_on_webflux.mapper.IndividualMapper;
import net.ilya.users_api_microservice_on_webflux.repository.*;
import net.ilya.users_api_microservice_on_webflux.util.DateUtilsService;
import org.junit.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgresTestContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IndividualsRestControllerV1IT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private IndividualMapper individualMapper;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IndividualRepository individualRepository;
    @Autowired
    private ProfileHistoryRepository profileHistoryRepository;
    @Autowired
    private VerificationStatusRepository verificationStatusRepository;

    @BeforeEach
    void setUp() {
        individualRepository.deleteAll().block();
        profileHistoryRepository.deleteAll().block();
        verificationStatusRepository.deleteAll().block();
        userRepository.deleteAll().block();
        addressRepository.deleteAll().block();
        countryRepository.deleteAll().block();
    }

    @Test
    @Order(1)
    @DisplayName("Successful Request when individual exist")
    void getIndividualById() {
        //        given
        Country country = countryRepository.save(DateUtilsService.getCountryWithDate()).block();
        assert country != null;
        Address address = addressRepository.save(DateUtilsService.getAddressWithDate().toBuilder()
                .countryId(country.getId())
                .build()).block();
        assert address != null;
        User user = userRepository.save(DateUtilsService.getUserWithDate().toBuilder()
                .addressId(address.getId())
                .build()).block();
        assert user != null;
        Individual individual = individualRepository.save(DateUtilsService.getIndividualWithDate().toBuilder()
                .userId(user.getId())
                .build()).block();
        //when
        assert individual != null;
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/individuals/{id}", individual.getId())
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.passport_number").isEqualTo(individual.getPassportNumber())
                .jsonPath("$.phone_number").isEqualTo(individual.getPhoneNumber());
    }

    @Test
    @Order(2)
    @DisplayName("Unsuccessful Request when individual not exist")
    void getIndividualByIdWhenNotExist() {
        UUID uuid = UUID.randomUUID();
        IndividualNonExistentById error = new IndividualNonExistentById(uuid);
        ErrorResponse errorBuild = ErrorResponse.builder()
                .errorCode(400)
                .message("No such individual :" + error.getUuid())
                .build();
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/individuals/{id}", uuid)
                .exchange();
        //then
        response.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorBuild.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Successful Request on register individual")
    void registeredIndividual() {
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
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .post()
                .uri("/api/v1/individuals/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(individualMapper.map(individual)), IndividualDto.class)
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.user_id").isNotEmpty()
                .jsonPath("$.passport_number").isEqualTo(individual.getPassportNumber())
                .jsonPath("$.phone_number").isEqualTo(individual.getPhoneNumber());
    }

    @Test
    @Order(4)
    @DisplayName("Unsuccessful Request on register individual when user duplicate")
    void registeredIndividualWhenUserNotUniq() {
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

        userRepository.save(user.toBuilder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .build()).block();

        DuplicateResourceException error = new DuplicateResourceException(String.format("%s,%s,%s",user.getFirstName(), user.getLastName(), user.getSecretKey()));
        ErrorResponse errorBuild = ErrorResponse.builder()
                .errorCode(400)
                .message(String.format("Duplicate date %s", error.getErrorEntity()))
                .build();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .post()
                .uri("/api/v1/individuals/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(individualMapper.map(individual)), IndividualDto.class)
                .exchange();
        //then
        response.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorBuild.getMessage());

    }

    @Test
    @Order(5)
    @DisplayName("Unsuccessful Request on register individual when individual duplicate")
    void registeredIndividualWhenIndividualNotUniq() {
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

        individualRepository.save(DateUtilsService.getIndividualWithDate()).block();

        DuplicateResourceException error = new DuplicateResourceException(String.format("%s,%s,%s", individual.getEmail(), individual.getPassportNumber(), individual.getPhoneNumber()));
        ErrorResponse errorBuild = ErrorResponse.builder()
                .errorCode(400)
                .message(String.format("Duplicate date %s", error.getErrorEntity()))
                .build();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .post()
                .uri("/api/v1/individuals/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(individualMapper.map(individual)), IndividualDto.class)
                .exchange();
        //then
        response.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorBuild.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Successful Request on update individual when individual exist")
    void updateWhenIndividualExist() {
        //given
        Country country = countryRepository.save(DateUtilsService.getCountryWithDate()).block();
        assert country != null;
        Address address = addressRepository.save(DateUtilsService.getAddressWithDate().toBuilder()
                .countryId(country.getId())
                .build()).block();
        assert address != null;
        User user = userRepository.save(DateUtilsService.getUserWithDate().toBuilder()
                .addressId(address.getId())
                .build()).block();
        assert user != null;
        Individual individual = individualRepository.save(DateUtilsService.getIndividualWithDate().toBuilder()
                .userId(user.getId())
                .build()).block();
        //when
        assert individual != null;
        /// Update
        Country countryUpdate = Country.builder()
                .id(country.getId())
                .name("Soviet Union")
                .alpha2("US")
                .alpha3("USS")
                .build();

        Address addressUpdate = Address.builder()
                .id(address.getId())
                .countryId(country.getId())
                .address("Red Street 10")
                .zipCode("320080")
                .city("Moscow")
                .country(countryUpdate)
                .state("USSR")
                .build();

        User userUpdate = User.builder()
                .id(user.getId())
                .addressId(address.getId())
                .secretKey("New age 2")
                .firstName("Leonid")
                .lastName("Lenin")
                .address(addressUpdate)
                .build();

        Individual individualUpdate = Individual.builder()
                .id(individual.getId())
                .userId(user.getId())
                .userData(userUpdate)
                .passportNumber("12-34 445-567")
                .phoneNumber("7-495-356-12-34")
                .email("ussr@foo.ru")
                .status(StatusEntity.ACTIVE)
                .build();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .put()
                .uri("/api/v1/individuals/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(individualMapper.map(individualUpdate)), IndividualDto.class)
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.passport_number").isEqualTo(individualUpdate.getPassportNumber())
                .jsonPath("$.phone_number").isEqualTo(individualUpdate.getPhoneNumber())
                .jsonPath("$.email").isEqualTo(individualUpdate.getEmail())
                .jsonPath("$.status").isEqualTo(StatusEntity.UPDATED.name())
                .jsonPath("$.user_id").isNotEmpty();
    }

    @Test
    @Order(7)
    @DisplayName("Unsuccessful Request on update individual when individual not exist")
    void updateWhenIndividualNotExist() {
        //given

        UUID uuid = UUID.randomUUID();
        Individual individualUpdate = Individual.builder()
                .id(uuid)
                .passportNumber("12-34 445-567")
                .phoneNumber("7-495-356-12-34")
                .email("ussr@foo.ru")
                .status(StatusEntity.ACTIVE)
                .build();

        IndividualNonExistentById error = new IndividualNonExistentById(uuid);
        ErrorResponse errorBuild = ErrorResponse.builder()
                .errorCode(400)
                .message("No such individual :" + error.getUuid())
                .build();

        //when
        WebTestClient.ResponseSpec response = webTestClient
                .put()
                .uri("/api/v1/individuals/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(individualMapper.map(individualUpdate)), IndividualDto.class)
                .exchange();
        //then
        response.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorBuild.getMessage());

    }

    @Test
    @Order(8)
    @DisplayName("Unsuccessful Request on deleted individual when individual not exist")
    void deleteWhenIndividualNotExist() {
        UUID uuid = UUID.randomUUID();
        IndividualNonExistentById error = new IndividualNonExistentById(uuid);
        ErrorResponse errorBuild = ErrorResponse.builder()
                .errorCode(400)
                .message("No such individual :" + error.getUuid())
                .build();
        WebTestClient.ResponseSpec response = webTestClient
                .delete()
                .uri("/api/v1/individuals/{id}", uuid)
                .exchange();
        //then
        response.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorBuild.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Successful Request on deleted individual when individual exist")
    void deleteWhenIndividualExist() {
        //        given
        Country country = countryRepository.save(DateUtilsService.getCountryWithDate()).block();
        assert country != null;
        Address address = addressRepository.save(DateUtilsService.getAddressWithDate().toBuilder()
                .countryId(country.getId())
                .build()).block();
        assert address != null;
        User user = userRepository.save(DateUtilsService.getUserWithDate().toBuilder()
                .addressId(address.getId())
                .build()).block();
        assert user != null;
        Individual individual = individualRepository.save(DateUtilsService.getIndividualWithDate().toBuilder()
                .userId(user.getId())
                .build()).block();
        //when
        assert individual != null;
        WebTestClient.ResponseSpec response = webTestClient
                .delete()
                .uri("/api/v1/individuals/{id}", individual.getId())
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.passport_number").isEqualTo(individual.getPassportNumber())
                .jsonPath("$.phone_number").isEqualTo(individual.getPhoneNumber())
                .jsonPath("$.email").isEqualTo(individual.getEmail())
                .jsonPath("$.status").isEqualTo(StatusEntity.DELETED.name())
                .jsonPath("$.user_id").isNotEmpty();
        ;
    }

    @Test
    @Order(10)
    @DisplayName("Successful Request on findAll individual when individual exist")
    void findAllWhenIndividualExist() {

        Country country = countryRepository.save(DateUtilsService.getCountryWithDate()).block();
        assert country != null;
        Address address = addressRepository.save(DateUtilsService.getAddressWithDate().toBuilder()
                .countryId(country.getId())
                .build()).block();
        assert address != null;
        User user = userRepository.save(DateUtilsService.getUserWithDate().toBuilder()
                .addressId(address.getId())
                .build()).block();
        assert user != null;
        Individual individual = individualRepository.save(DateUtilsService.getIndividualWithDate().toBuilder()
                .userId(user.getId())
                .build()).block();
        //when
        assert individual != null;
        Country country1 = countryRepository.save(DateUtilsService.getCountryWithDate()).block();
        assert country1 != null;
        Address address1 = addressRepository.save(DateUtilsService.getAddressWithDate().toBuilder()
                .countryId(country1.getId())
                .build()).block();
        assert address1 != null;
        User user1 = userRepository.save(DateUtilsService.getUserWithDate().toBuilder()
                .addressId(address1.getId())
                .build()).block();
        assert user1 != null;
        Individual individual1 = individualRepository.save(DateUtilsService.getIndividualWithDate().toBuilder()
                .userId(user1.getId())
                .build()).block();
        //when
        assert individual1 != null;
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/individuals/list")
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(individual.getId().toString())
                .jsonPath("$.[1].id").isEqualTo(individual1.getId().toString());
    }

    @Test
    @Order(11)
    @DisplayName("Successful Request on verified individual when individual exist")
    void verifiedIndividualWhenIndividualExist() {

        Country country = countryRepository.save(DateUtilsService.getCountryWithDate()).block();
        assert country != null;
        Address address = addressRepository.save(DateUtilsService.getAddressWithDate().toBuilder()
                .countryId(country.getId())
                .country(country)
                .build()).block();
        assert address != null;
        User user = userRepository.save(DateUtilsService.getUserWithDate().toBuilder()
                .addressId(address.getId())
                .address(address)
                .build()).block();
        assert user != null;
        VerificationStatus verificationStatus = verificationStatusRepository.save(VerificationStatus.builder()
                .profileId(user.getId())
                .profileType(ProfileType.INDIVIDUAL)
                .updated(LocalDateTime.now())
                .created(LocalDateTime.now())
                .verificationStatus(VerificationEntityStatus.UNVERIFIED)
                .build()).block();
        ProfileHistory block = profileHistoryRepository.save(ProfileHistory.builder()
                .profileId(user.getId())
                .profileType(ProfileType.INDIVIDUAL)
                .created(LocalDateTime.now())
                .comment("Create")
                .reason(HistoryReason.CREATE.name())
                .changedValues("TEST")
                .build()).block();

        Individual individual = individualRepository.save(DateUtilsService.getIndividualWithDate().toBuilder()
                .userId(user.getId())
                .userData(user)
                .build()).block();
        User userWithOutDate = User.builder()
                .id(user.getId())
                .secretKey(user.getSecretKey())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .addressId(address.getId())
                .build();
        assert individual != null;
        Individual requestBody = Individual.builder()
                .id(individual.getId())
                .email(individual.getEmail())
                .passportNumber(individual.getPassportNumber())
                .phoneNumber(individual.getPhoneNumber())
                .userData(userWithOutDate)
                .userId(userWithOutDate.getId())
                .build();
        //when
        assert requestBody != null;
        WebTestClient.ResponseSpec response = webTestClient
                .put()
                .uri("/api/v1/individuals/verified")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(individualMapper.map(requestBody)), IndividualDto.class)
                .exchange();

        //check changes in verification status entity;

        StepVerifier
                .create(verificationStatusRepository.findByProfileId(userWithOutDate.getId()))
                .expectNextMatches(verificationStatus1 -> verificationStatus1.getVerificationStatus().equals(VerificationEntityStatus.VERIFIED) |
                        verificationStatus1.getUpdated().isAfter(individual.getVerifiedAt()))
                .expectComplete()
                .verify();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.user_id").isNotEmpty();
    }

}