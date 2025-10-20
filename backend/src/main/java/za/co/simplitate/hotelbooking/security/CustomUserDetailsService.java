package za.co.simplitate.hotelbooking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.exceptions.NotFoundException;
import za.co.simplitate.hotelbooking.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found!!"));
        return AuthUser.builder()
                .user(user)
                .build();
    }
}
