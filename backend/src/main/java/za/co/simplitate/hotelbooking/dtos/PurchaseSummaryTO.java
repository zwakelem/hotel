package za.co.simplitate.hotelbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseSummaryTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
    private Long purchaseCount;
    private String period; // DAY, WEEK, MONTH, YEAR
}
