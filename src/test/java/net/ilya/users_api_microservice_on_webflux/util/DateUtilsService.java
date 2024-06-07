package net.ilya.users_api_microservice_on_webflux.util;

import net.ilya.users_api_microservice_on_webflux.dto.*;
import net.ilya.users_api_microservice_on_webflux.entity.*;
import net.ilya.users_api_microservice_on_webflux.entity.MemberRole;
import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class DateUtilsService {
    public static Country getCountry1() {
        return Country.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .name("test")
                .alpha2("TST")
                .alpha3("EUR")
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static CountryDto getCountryDto1() {
        return CountryDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .name("test")
                .alpha2("TST")
                .alpha3("EUR")
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static Country getCountryWithoutDate(){
        return Country.builder()
                .name("RUSSIA")
                .alpha2("RU")
                .alpha3("RUS")
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static Country getCountryWithDate(){
        return Country.builder()
                .name("RUSSIA")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .alpha2("RU")
                .alpha3("RUS")
                .status(StatusEntity.ACTIVE)
                .build();
    }

    public static Country getCountryForCreatedMethodAfterEntity() {
        return Country.builder()
                .name("test")
                .alpha2("TST")
                .alpha3("EUR")
                .build();
    }

    public static Country getCountryForCreatedMethodBeforeEntity() {
        return Country.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .name("test")
                .alpha2("TST")
                .alpha3("EUR")
                .status(StatusEntity.ACTIVE)
                .build();
    }

    public static Address getAddress1() {
        return Address.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .countryId(1L)
                .address("St.White 3")
                .zipCode("333")
                .city("New York")
                .build();
    }
    public static AddressDto getAddressDto1() {
        return AddressDto.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .countryId(1L)
                .address("St.White 3")
                .zipCode("333")
                .city("New York")
                .build();
    }
    public static Address getAddressWithDate(){
        return Address.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .address("Tisovaya Street 8")
                .zipCode("350087")
                .archived(LocalDateTime.now())
                .city("Moscow")
                .state("Russian Federation")
                .build();
    }
    public static User getUserWithDate(){
        return User.builder()
                .secretKey("Top Fine")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .firstName("Petr")
                .lastName("Albertov")
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .filled(false)
                .build();
    }
    public static User getUserWithOutDate(){
        return User.builder()
                .secretKey("Top Fine")
                .firstName("Petr")
                .lastName("Albertov")
                .status(StatusEntity.ACTIVE)
                .filled(false)
                .build();
    }
    public static Individual getIndividualWithDate(){
        return Individual.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("test@foo.com")
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static Individual getIndividualWithOutDate(){
        return Individual.builder()
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("test@foo.com")
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static Address getAddressWithOutDate(){
        return Address.builder()
                .address("Tisovaya Street 8")
                .zipCode("350087")
                .city("Moscow")
                .state("Russian Federation")
                .build();
    }
    public static Address getAddress2() {
        return Address.builder()
                .countryId(1L)
                .address("St.White 3")
                .zipCode("333")
                .city("New York")
                .build();
    }

    public static User getUser1() {
        return User.builder()
                .id(UUID.randomUUID())
                .secretKey("Key")
                .firstName("Petr")
                .lastName("Frolov")
                .addressId(UUID.randomUUID())
                .build();
    }
    public static User getUserWithOutId() {
        return User.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .secretKey("Key")
                .firstName("Petr")
                .lastName("Frolov")
                .status(StatusEntity.ACTIVE)
                .filled(false)
                .build();
    }
    public static UserDto getUserDto1() {
        return UserDto.builder()
                .id(UUID.randomUUID())
                .secretKey("Key")
                .firstName("Petr")
                .lastName("Frolov")
                .addressId(UUID.randomUUID())
                .build();
    }
    public static VerificationStatus getVerificationStatusForUser1(){
        return VerificationStatus.builder()
                .id(UUID.randomUUID())
                .profileType(ProfileType.INDIVIDUAL)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verificationStatus(VerificationEntityStatus.UNVERIFIED)
                .build();
    }
    public static User getUser2() {
        return User.builder()
                .id(UUID.randomUUID())
                .status(StatusEntity.ACTIVE)
                .secretKey("Key1")
                .firstName("Oleg")
                .lastName("Yarov")
                .addressId(UUID.randomUUID())
                .build();
    }

    public static ProfileHistory getProfileHistory1() {
        return ProfileHistory.builder()
                .profileType(ProfileType.INDIVIDUAL)
                .reason("reason one")
                .comment("For reason")
                .changedValues("Changed")
                .build();
    }

    public static Individual getIndividual1() {
        return Individual.builder()
                .userId(UUID.randomUUID())
                .phoneNumber("+4358999875")
                .passportNumber("1234456789")
                .email("foo@google.com")
                .build();
    }
    public static IndividualDto getIndividualDto1() {
        return IndividualDto.builder()
                .userId(UUID.randomUUID())
                .phoneNumber("+4358999875")
                .passportNumber("1234456789")
                .email("foo@google.com")
                .build();
    }

    public static MerchantMember getMerchantMemberWithDateActive(){
        return MerchantMember.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .memberRole(MemberRole.MANAGER)
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static MerchantMember getMerchantMemberWithOutDateActive(){
        return MerchantMember.builder()
                .memberRole(MemberRole.MANAGER)
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static MerchantMemberDto getMerchantMemberDtoWithDateActive(){
        return MerchantMemberDto.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .memberRole(MemberRole.MANAGER)
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static MerchantMemberDto getMerchantMemberDtoWithOutDateActive(){
        return MerchantMemberDto.builder()
                .memberRole(MemberRole.ADMINISTRATOR)
                .build();
    }
    public static Merchant getMerchantWithDate(){
        return Merchant.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .companyName("WD")
                .companyId(1L)
                .email("wd@foo.com")
                .phoneNumber("+345234562456")
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .filled(false)
                .build();
    }
    public static Merchant getMerchantWithOutDate(){
        return Merchant.builder()
                .companyId(1L)
                .companyName("WD")
                .email("wd@foo.com")
                .phoneNumber("+345234562456")
                .filled(false)
                .build();
    }
    public static MerchantDto getMerchantDtoWithDate(){
        return MerchantDto.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .companyName("WD")
                .companyId(1L)
                .email("wd@foo.com")
                .phoneNumber("+345234562456")
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .filled(false)
                .build();
    }
    public static MerchantDto getMerchantDtoWithOutDate(){
        return MerchantDto.builder()
                .companyName("WD")
                .companyId(1L)
                .email("wd@foo.com")
                .phoneNumber("+345234562456")
                .filled(false)
                .build();
    }
    public static MerchantMemberInvitations getMerchantMemberInvitationActive1(){
        return MerchantMemberInvitations.builder()
                .created(LocalDateTime.now())
                .expires(LocalDateTime.now())
                .firstName("John")
                .lastName("Cena")
                .email("cena_john@mail.com")
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static MerchantMemberInvitations getMerchantMemberInvitationCreate(){
        return MerchantMemberInvitations.builder()
                .expires(LocalDateTime.now())
                .firstName("John")
                .lastName("Cena")
                .email("cena_john@mail.com")
                .build();
    }
    public static User getUserAfterGiveInvitationWithDate(){
        return User.builder()
                .secretKey("Key Test")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .firstName("John")
                .lastName("Cena")
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .filled(false)
                .build();
    }
    public static User getUserAfterGiveInvitationWithOutDate(){
        return User.builder()
                .secretKey("Key Test")
                .firstName("John")
                .lastName("Cena")
                .build();
    }
    public static UserDto getUserDtoAfterGiveInvitationWithDate(){
        return UserDto.builder()
                .secretKey("Key Test")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .firstName("John")
                .lastName("Cena")
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .filled(false)
                .build();
    }
    public static UserDto getUserDtoAfterGiveInvitationWithOutDate(){
        return UserDto.builder()
                .secretKey("Key Test")
                .firstName("John")
                .lastName("Cena")
                .build();
    }
    public static UUID getRandomId(){
        return UUID.randomUUID();
    }
}
