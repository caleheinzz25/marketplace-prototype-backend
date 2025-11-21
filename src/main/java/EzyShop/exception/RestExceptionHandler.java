package EzyShop.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

// AuthenticationException
// BusinessException
// DuplicateResourceException
// InvalidTokenException
// ResourceNotFoundException
// UnauthorizedException
@ControllerAdvice
public class RestExceptionHandler {

        // VALIDATION ERROR
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getDefaultMessage())
                                .toList();

                return buildErrorResponse(
                                400,
                                "Validation failed",
                                errorMessages,
                                request.getRequestURI());
        }

        // TYPE MISMATCH ERROR (e.g., ?page=abc instead of ?page=1)
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {
                String errorMsg = String.format(
                                "Invalid type for parameter '%s': expected %s but got '%s'",
                                ex.getName(),
                                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                                ex.getValue());

                return buildErrorResponse(
                                400,
                                "Type mismatch",
                                List.of(errorMsg),
                                request.getRequestURI());
        }

        // ILLEGAL ARGUMENT ERROR
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
                return buildErrorResponse(
                                400,
                                "Invalid argument",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

        // GENERAL EXCEPTION HANDLER
        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleGeneralError(Exception ex, HttpServletRequest request) {
                ex.printStackTrace(); // Log for debugging

                return buildErrorResponse(
                                500,
                                "Internal server error",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

        // DUPLICATE RESOUCE HANDLER
        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<?> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
                return buildErrorResponse(
                                409, // Conflict
                                "Duplicate resource",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

        // Common method for formatting error responses
        private ResponseEntity<Map<String, Object>> buildErrorResponse(int status, String message, List<String> errors,
                        String path) {
                return ResponseEntity.status(status).body(Map.of(
                                "timestamp", Instant.now().toString(),
                                "status", status,
                                "message", message,
                                "errors", errors,
                                "path", path));
        }

        // AUTHENTICATION ERROR
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
                return buildErrorResponse(
                                401,
                                "Authentication failed",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

        // RESOURCE NOT FOUND
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
                return buildErrorResponse(
                                404,
                                "Resource not found",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

        // INVALID TOKEN
        @ExceptionHandler(InvalidTokenException.class)
        public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex, HttpServletRequest request) {
                return buildErrorResponse(
                                403,
                                "Invalid token",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

        // CUSTOM BUSINESS ERROR (e.g., saldo tidak cukup, dll.)
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<?> handleBusinessException(BusinessException ex, HttpServletRequest request) {
                return buildErrorResponse(
                                ex.getStatus().value(),
                                "Business logic error",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

        // UNAUTHORIZED ACCESS ERROR
        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
                return buildErrorResponse(
                                401,
                                "Unauthorized",
                                List.of(ex.getMessage()),
                                request.getRequestURI());
        }

}
