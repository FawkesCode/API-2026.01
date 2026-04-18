package com.fawkes.api.Helpers;
import com.fawkes.api.Entities.PaymentMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class PaymentConverter implements AttributeConverter<Set<PaymentMethod>, String> {
    private static final String SPLIT_CHAR = ",";
    @Override
    public String convertToDatabaseColumn(Set<PaymentMethod> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(PaymentMethod::name)
                .collect(Collectors.joining(SPLIT_CHAR));
    }
    @Override
    public Set<PaymentMethod> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .map(PaymentMethod::valueOf)
                .collect(Collectors.toSet());
    }
}
