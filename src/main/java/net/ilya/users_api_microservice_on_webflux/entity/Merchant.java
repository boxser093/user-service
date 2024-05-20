package net.ilya.users_api_microservice_on_webflux.entity;

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
@Table("person.merchants")
public class Merchant {
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
    private User creator;
}
