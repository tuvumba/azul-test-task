package dev.tuvumba.azul_test_task.config.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


/**
 *  Wrapper for error responses. <br>
 *  Holds the status, message and a debugMessage.
 */
@Getter
@Setter
@AllArgsConstructor
public class ApiErrorResponse {
    @Schema(example = "404 NOT FOUND")
    private HttpStatus status;
    @Schema(example = "The error occurred here and there.")
    private String message;
    @Schema(example = "This is the detailed debug message.")
    private String debugMessage;

    public ApiErrorResponse(HttpStatus status, String message, Throwable ex) {
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
}
