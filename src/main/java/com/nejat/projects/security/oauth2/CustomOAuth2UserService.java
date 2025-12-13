package com.nejat.projects.security.oauth2;

import com.nejat.projects.user.AuthProvider;
import com.nejat.projects.user.Role;
import com.nejat.projects.user.User;
import com.nejat.projects.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String givenName = (String) attributes.getOrDefault("given_name", "");
        String familyName = (String) attributes.getOrDefault("family_name", "");

        User user = userRepository.findByEmail(email).orElseGet(() -> createGoogleUser(email, givenName, familyName));
        if (user.getAuthProvider() != AuthProvider.GOOGLE) {
            throw new OAuth2AuthenticationException("Account registered with different provider");
        }
        Set<Role> authorities = user.getRoles().isEmpty() ? new HashSet<>(Set.of(Role.ROLE_USER)) : user.getRoles();
        return new DefaultOAuth2User(authorities.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList(),
                attributes, "email");
    }

    private User createGoogleUser(String email, String firstName, String lastName) {
        User user = User.builder()
                .email(email)
                .password(null)
                .firstName(firstName)
                .lastName(lastName)
                .authProvider(AuthProvider.GOOGLE)
                .roles(Set.of(Role.ROLE_USER))
                .build();
        return userRepository.save(user);
    }
}
