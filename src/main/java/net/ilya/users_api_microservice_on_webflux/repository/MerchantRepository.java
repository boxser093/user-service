package net.ilya.users_api_microservice_on_webflux.repository;

import net.ilya.users_api_microservice_on_webflux.entity.Merchant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MerchantRepository extends R2dbcRepository<Merchant, UUID> {
    @Query("SELECT * FROM person.merchants where company_name=:companyName and email=:email and phone_number=:phoneNumber and company_id=:companyId FOR UPDATE")
    Mono<Merchant> findByCompanyNameAndEmailAndPhoneNumberAndCompanyId(String companyName, String email, String phoneNumber, Long companyId);
}
