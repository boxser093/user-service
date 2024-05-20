package net.ilya.users_api_microservice_on_webflux.error;


import net.ilya.users_api_microservice_on_webflux.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationErrorHandler {


    @ExceptionHandler(InvitationExpireError.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(InvitationExpireError e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .errorCode(404)
                        .message(String.format("Invitation has expired for id %s", e.getINVITATION()))
                        .build());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("Duplicate date %s", e.getErrorEntity()))
                        .build());
    }
    @ExceptionHandler(UserNotUniq.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(UserNotUniq e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("Try with other user date, this:%s, not uniq", e.getUser()))
                        .build());
    }

    @ExceptionHandler(IndividualNotUniq.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(IndividualNotUniq e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("Try with other individual date. Not uniq %s, %s, %s", e.getIndividual().getEmail(), e.getIndividual().getPassportNumber(), e.getIndividual().getPhoneNumber()))
                        .build());
    }

    @ExceptionHandler(IndividualNonExistentById.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(IndividualNonExistentById e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message("No such individual :" + e.getUuid())
                        .build());
    }

    @ExceptionHandler(IndividualNonExistent.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(IndividualNonExistent e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("Try with other individual date. Not uniq %s, %s, %s", e.getIndividual().getEmail(), e.getIndividual().getPassportNumber(), e.getIndividual().getPhoneNumber()))
                        .build());
    }

    @ExceptionHandler(ObjectNotExist.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(ObjectNotExist e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message("No such entity of :" + e.getUuid())
                        .build());
    }
}
