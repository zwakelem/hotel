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
public class BudgetStatusTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BudgetPeriod period;
    private BigDecimal percentageUsed;
}
