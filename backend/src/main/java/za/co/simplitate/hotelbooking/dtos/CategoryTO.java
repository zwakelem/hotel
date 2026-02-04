package za.co.simplitate.hotelbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTO {
    private Long id;
    private String name;
    private String description;
    private Long userId;
}
