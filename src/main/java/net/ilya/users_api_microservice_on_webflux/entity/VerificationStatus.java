package net.ilya.users_api_microservice_on_webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("person.verification_statuses")
public class VerificationStatus {
    @Id
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private UUID profileId;
    private ProfileType profileType;
    private String details;
    private VerificationEntityStatus verificationStatus;
}
