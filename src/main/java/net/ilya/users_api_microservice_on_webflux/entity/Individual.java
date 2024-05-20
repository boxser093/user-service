package net.ilya.users_api_microservice_on_webflux.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("person.individuals")
public class Individual {
    @Id
    private UUID id;
    private UUID userId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String passportNumber;
    private String phoneNumber;
    private String email;
    private LocalDateTime verifiedAt;
    private LocalDateTime archivedAt;
    private StatusEntity status;

    @Transient
    @ToString.Exclude
    private User userData;
}
