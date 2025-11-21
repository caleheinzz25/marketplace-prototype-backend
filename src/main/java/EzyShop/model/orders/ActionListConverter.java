package EzyShop.model.orders;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import EzyShop.dto.payment.ActionDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ActionListConverter implements AttributeConverter<List<ActionDto>, String> {

    private final ObjectMapper mapper = new ObjectMapper(); // inisialisasi langsung

    @Override
    public String convertToDatabaseColumn(List<ActionDto> actions) {
        try {
            return mapper.writeValueAsString(actions);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert actions to JSON", e);
        }
    }

    @Override
    public List<ActionDto> convertToEntityAttribute(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert JSON to actions", e);
        }
    }
}
