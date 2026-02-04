package za.co.simplitate.hotelbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.simplitate.hotelbooking.util.enums.BudgetPeriod;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private BudgetPeriod period;
    private Long userId;
}
