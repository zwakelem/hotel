package za.co.simplitate.hotelbooking.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import za.co.simplitate.hotelbooking.util.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response(
        // generic
        int statusCode,
        String message,

        // for login
        String token,
        UserRole role,
        boolean active,
        String expirationTime,

        // user data
        UserTO user,
        List<UserTO> users,

        // booking data
        BookingTO booking,
        List<BookingTO> bookings,

        // Room data
        RoomTO room,
        List<RoomTO> rooms,

        // Payments data
        String transactionId,
        PaymentTO payment,
        List<PaymentTO> payments,

        // Finance data - Category
        CategoryTO category,
        List<CategoryTO> categoryList,

        // Finance data - Purchase
        PurchaseTO purchase,
        List<PurchaseTO> purchaseList,
        PurchaseSummaryTO purchaseSummary,

        // Finance data - Budget
        BudgetTO budget,
        List<BudgetTO> budgetList,
        BudgetStatusTO budgetStatus,
        List<BudgetStatusTO> budgetStatusList,

        LocalDateTime timestamp
) { }
