package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import za.co.simplitate.hotelbooking.util.enums.UserRole;

import java.time.LocalDate;

@Entity
@Table(name="users")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;

    private String lastName;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "phoneNumber is required")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean isActive;

    private final LocalDate createdAt = LocalDate.now();

}
