package net.ilya.users_api_microservice_on_webflux.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class InvitationExpireError extends RuntimeException{
    private final UUID INVITATION;
    private static final String MESSAGE = "Invitation is expire";
}
