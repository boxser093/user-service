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
@Table("person.profile_history")
public class ProfileHistory {
    @Id
    private UUID id;
    private LocalDateTime created;
    private UUID profileId;
    private ProfileType profileType;
    private String reason;
    private String comment;
    private String changedValues;
}
