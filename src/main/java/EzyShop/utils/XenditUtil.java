package EzyShop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Setter
@Component
@ConfigurationProperties(prefix = "xendit")
@RequiredArgsConstructor
public class XenditUtil {

    private String secretKey;
    private final ObjectMapper mapper;

    /**
     * Build HTTP headers for Xendit API request
     */
    public HttpHeaders buildHeaders() {
        log.info(secretKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, ""); // Spring handles Base64 encoding
        headers.set("api-version", "2024-11-11");
        return headers;
    }

    /**
     * Alternative manual base64 encoding for debugging
     */
    public String encodeBasicAuth(String secretKey) {
        String raw = secretKey + ":";
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Format current time + duration as ISO 8601 UTC
     */
    /**
     * Return future timestamp as ZonedDateTime in UTC
     */
    public ZonedDateTime generateFutureTimestamp(int minutesAhead) {
        return ZonedDateTime.now(ZoneOffset.UTC).plusHours(minutesAhead);
    }

    /**
     * Convert object to JSON string
     */
    @SneakyThrows
    public String toJson(Object obj) {
        return mapper.writeValueAsString(obj);
    }

    /**
     * Convert JSON string to object
     */
    @SneakyThrows
    public <T> T fromJson(String json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }
}
