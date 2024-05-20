package net.ilya.users_api_microservice_on_webflux.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.ilya.users_api_microservice_on_webflux.entity.StatusEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Merchant DTO")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantDto {
    @Id
    private UUID id;
    private UUID creatorId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String companyName;
    private Long companyId;
    private String email;
    private String phoneNumber;
    private LocalDateTime verifiedAt;
    private LocalDateTime archivedAt;
    private StatusEntity status;
    private boolean filled;

    @Transient
    @ToString.Exclude
    private UserDto creator;
}
