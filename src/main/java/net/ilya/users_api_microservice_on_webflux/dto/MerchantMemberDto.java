package net.ilya.users_api_microservice_on_webflux.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.ilya.users_api_microservice_on_webflux.entity.MemberRole;
import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "MerchantMember DTO")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantMemberDto {

    @Id
    private UUID id;
    private UUID userId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private UUID merchantId;
    private MemberRole memberRole;
    private StatusEntity status;

    @ToString.Exclude
    private MerchantDto merchant;

    @ToString.Exclude
    private UserDto user ;
}
