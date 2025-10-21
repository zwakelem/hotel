package za.co.simplitate.hotelbooking.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import za.co.simplitate.hotelbooking.util.enums.RoomType;

import java.math.BigDecimal;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoomTO(
        Long id,
        Integer roomNumber,
        RoomType roomType,
        BigDecimal pricePerNight,
        Integer capacity,
        String description,
        String imageUrl
) {
}
