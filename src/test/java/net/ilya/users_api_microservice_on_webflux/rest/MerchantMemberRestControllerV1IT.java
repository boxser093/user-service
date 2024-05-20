package net.ilya.users_api_microservice_on_webflux.rest;

import net.ilya.users_api_microservice_on_webflux.config.PostgresTestContainerConfig;
import net.ilya.users_api_microservice_on_webflux.dto.ErrorResponse;
import net.ilya.users_api_microservice_on_webflux.dto.MerchantDto;
import net.ilya.users_api_microservice_on_webflux.dto.MerchantMemberDto;
import net.ilya.users_api_microservice_on_webflux.dto.UserDto;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.error.ObjectNotExist;
import net.ilya.users_api_microservice_on_webflux.mapper.MerchantMemberMapper;
import net.ilya.users_api_microservice_on_webflux.repository.*;
import net.ilya.users_api_microservice_on_webflux.util.DateUtilsService;
import org.junit.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mock;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgresTestContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MerchantMemberRestControllerV1IT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private IndividualRepository individualRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantMemberInvitationRepository invitationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MerchantMemberRepository merchantMemberRepository;
    @Autowired
    private ProfileHistoryRepository profileHistoryRepository;
    @Autowired
    private VerificationStatusRepository verificationStatusRepository;

    @BeforeEach
    void setUp() {
        individualRepository.deleteAll().block();
        merchantMemberRepository.deleteAll().block();
        invitationRepository.deleteAll().block();
        merchantRepository.deleteAll().block();
        profileHistoryRepository.deleteAll().block();
        verificationStatusRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }


    @Test
    @Order(1)
    @DisplayName("Successful Request when Member exist")
    void getMerchantMemberById() {
        //given
        User user = userRepository.save(DateUtilsService.getUserAfterGiveInvitationWithDate()).block();
        assert user != null;
        MerchantMember save = merchantMemberRepository.save(DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .userId(user.getId())
                .build()).block();
        assert save != null;
        UUID id = save.getId();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/members/{id}", id)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(id.toString())
                .jsonPath("$.user_id").isEqualTo(user.getId().toString());
    }

    @Test
    @Order(2)
    @DisplayName("Unsuccessful Request when Member not exist")
    void getMerchantMemberByIdWhenNotExist() {
        //given
        UUID id = UUID.randomUUID();

        ObjectNotExist error = new ObjectNotExist(id.toString());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(400)
                .message("No such entity of :" + error.getUuid())
                .build();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/members/{id}", id)
                .exchange();
        //then
        response.
                expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorResponse.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Successful Request when Merchant exist and Invocation hasn't expired")
    void registerMerchantMember() {
        //give
        LocalDateTime expired = LocalDateTime.of(2025, 5, 18, 11, 20);
        User userMerchant = userRepository.save(DateUtilsService.getUserAfterGiveInvitationWithDate().toBuilder().build()).block();
        assert userMerchant != null;
        Merchant merchant = merchantRepository.save(DateUtilsService.getMerchantWithDate().toBuilder()
                .creatorId(userMerchant.getId())
                .build()).block();
        assert merchant != null;
        MerchantMemberInvitations invitations = invitationRepository.save(DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .expires(expired)
                .merchantId(merchant.getId())
                .build()).block();
        assert invitations != null;
        UUID invitationsId = invitations.getId();

        //when
        WebTestClient.ResponseSpec exchange = webTestClient
                .post()
                .uri("/api/v1/members/{id}", invitationsId)
                .exchange();
        //then
        exchange.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.user_id").isNotEmpty()
                .jsonPath("$.merchant_id").isNotEmpty()
                .jsonPath("$.status").isEqualTo(StatusEntity.ACTIVE.name())
                .jsonPath("$.created").isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Unsuccessful Request when Merchant exist and Invocation not Exist")
    void registerMerchantMemberWhenInvitationNotExist() {
        //given
        UUID invitationsId = DateUtilsService.getRandomId();
        String messageError = "No such entity of :" + invitationsId;
        //when
        WebTestClient.ResponseSpec exchange = webTestClient
                .post()
                .uri("/api/v1/members/{id}", invitationsId)
                .exchange();
        //then
        exchange.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(messageError);
    }

    @Test
    @Order(5)
    @DisplayName("Unsuccessful Request when Merchant exist and Invocation has expired")
    void registerMerchantMemberWhenInvitationExpired() {
        //give
        LocalDateTime expired = LocalDateTime.of(2024, 5, 18, 11, 20);
        User userMerchant = userRepository.save(DateUtilsService.getUserAfterGiveInvitationWithDate().toBuilder().build()).block();
        assert userMerchant != null;
        Merchant merchant = merchantRepository.save(DateUtilsService.getMerchantWithDate().toBuilder()
                .creatorId(userMerchant.getId())
                .build()).block();
        assert merchant != null;
        MerchantMemberInvitations invitations = invitationRepository.save(DateUtilsService.getMerchantMemberInvitationActive1().toBuilder()
                .expires(expired)
                .merchantId(merchant.getId())
                .build()).block();
        assert invitations != null;
        UUID invitationsId = invitations.getId();
        String errorMessage = "Invitation has expired for id " + invitationsId;
        //when
        WebTestClient.ResponseSpec exchange = webTestClient
                .post()
                .uri("/api/v1/members/{id}", invitationsId)
                .exchange();
        //then
        exchange.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @Order(6)
    @DisplayName("Unsuccessful Request when Member not exist")
    void updateMerchantMemberWhenMerchantExistNotExist() {
        //given
        User user = userRepository.save(DateUtilsService.getUserAfterGiveInvitationWithDate()).block();
        assert user != null;
        MerchantMember save = merchantMemberRepository.save(DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .userId(user.getId())
                .build()).block();
        assert save != null;
        UserDto userDtoAfterGiveInvitationWithOutDate = DateUtilsService.getUserDtoAfterGiveInvitationWithOutDate().toBuilder()
                .id(user.getId())
                .firstName("First")
                .lastName("Leader")
                .secretKey("Joseph")
                .build();
        UUID noSuchMemberId = DateUtilsService.getRandomId();
        MerchantMemberDto merchantMemberDtoWithOutDateActive = DateUtilsService.getMerchantMemberDtoWithOutDateActive().toBuilder()
                .id(noSuchMemberId)
                .user(userDtoAfterGiveInvitationWithOutDate)
                .merchant(mock(MerchantDto.class))
                .build();
        String errorMessage = "No such entity of :" + noSuchMemberId;
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .put()
                .uri("/api/v1/members/")
                .body(Mono.just(merchantMemberDtoWithOutDateActive), MerchantMemberDto.class)
                .exchange();
        //then
        response.
                expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @Order(7)
    @DisplayName("Successful Request when Member exist")
    void updateMerchantMemberWhenMerchantExist() {
        //given
        User user = userRepository.save(DateUtilsService.getUserAfterGiveInvitationWithDate()).block();
        assert user != null;
        MerchantMember save = merchantMemberRepository.save(DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .userId(user.getId())
                .build()).block();
        assert save != null;
        UserDto userDtoAfterGiveInvitationWithOutDate = DateUtilsService.getUserDtoAfterGiveInvitationWithOutDate().toBuilder()
                .id(user.getId())
                .firstName("First")
                .lastName("Leader")
                .secretKey("Joseph")
                .build();
        MerchantMemberDto merchantMemberDtoWithOutDateActive = DateUtilsService.getMerchantMemberDtoWithOutDateActive().toBuilder()
                .id(save.getId())
                .user(userDtoAfterGiveInvitationWithOutDate)
                .merchant(mock(MerchantDto.class))
                .build();

        //when
        WebTestClient.ResponseSpec response = webTestClient
                .put()
                .uri("/api/v1/members/")
                .body(Mono.just(merchantMemberDtoWithOutDateActive), MerchantMemberDto.class)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(save.getId().toString())
                .jsonPath("$.user_id").isEqualTo(user.getId().toString())
                .jsonPath("$.member_role").isEqualTo(merchantMemberDtoWithOutDateActive.getMemberRole().name())
                .jsonPath("$.status").isEqualTo(StatusEntity.UPDATED.name());
    }

    @Test
    @Order(8)
    @DisplayName("Unsuccessful Request when Member now exist")
    void deleteMerchantMemberWhenNotExist() {
        //given
        UUID id = DateUtilsService.getRandomId();
        ;
        String errorMessage = "No such entity of :" + id;
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .delete()
                .uri("/api/v1/members/{id}", id)
                .exchange();
        //then
        response.
                expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @Order(9)
    @DisplayName("Successful Request when Member exist")
    void deleteMerchantMemberWhenExist() {
        //given
        User user = userRepository.save(DateUtilsService.getUserAfterGiveInvitationWithDate()).block();
        assert user != null;
        MerchantMember save = merchantMemberRepository.save(DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .userId(user.getId())
                .build()).block();
        assert save != null;
        UUID id = save.getId();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .delete()
                .uri("/api/v1/members/{id}", id)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(id.toString())
                .jsonPath("$.user_id").isEqualTo(user.getId().toString())
                .jsonPath("$.status").isEqualTo(StatusEntity.DELETED.name());
    }

    @Test
    @Order(10)
    @DisplayName("Successful Request when Members exist")
    void findAllMerchantMembers() {
        //given
        User user = userRepository.save(DateUtilsService.getUserAfterGiveInvitationWithDate()).block();
        assert user != null;
        MerchantMember merchantMember = merchantMemberRepository.save(DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .userId(user.getId())
                .build()).block();
        User user1 = userRepository.save(DateUtilsService.getUserWithOutId()).block();
        assert user1 != null;
        MerchantMember merchantMember1 = merchantMemberRepository.save(DateUtilsService.getMerchantMemberWithDateActive().toBuilder()
                .userId(user1.getId())
                .build()).block();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/members/list")
                .exchange();
        //then
        assert merchantMember != null;
        assert merchantMember1 != null;
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$[0].id").isEqualTo(merchantMember.getId().toString())
                .jsonPath("$[0].user_id").isEqualTo(user.getId().toString())
                .jsonPath("$[0].status").isEqualTo(StatusEntity.ACTIVE.name())
                .jsonPath("$[1].id").isEqualTo(merchantMember1.getId().toString())
                .jsonPath("$[1].user_id").isEqualTo(user1.getId().toString())
                .jsonPath("$[1].status").isEqualTo(StatusEntity.ACTIVE.name());
    }
}