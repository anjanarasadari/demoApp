package com.payable.demo.security;

import com.payable.demo.model.User;
import com.payable.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                user.getUserRoles().stream()
                        .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName().toUpperCase()))
                        .collect(Collectors.toList()),
                user.getId(),
                null // Placeholder for propertyId as it's not in the User entity
        );
    }
}
