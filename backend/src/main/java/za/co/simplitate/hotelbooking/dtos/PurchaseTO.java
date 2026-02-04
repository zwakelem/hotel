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
public class PurchaseTO {
    private Long id;
    private LocalDate purchaseDate;
    private String merchant;
    private String merchantAddress;
    private BigDecimal price;
    private BigDecimal vat;
    private BigDecimal total;
    private Integer quantity;
    private String link;
    private Long categoryId;
    private String categoryName;
    private Long userId;
}
