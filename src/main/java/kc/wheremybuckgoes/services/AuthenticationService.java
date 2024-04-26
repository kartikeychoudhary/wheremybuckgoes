package kc.wheremybuckgoes.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.Token;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.repositories.TokenRepository;
import kc.wheremybuckgoes.repositories.UserRepository;
import kc.wheremybuckgoes.request.AuthenticationRequest;
import kc.wheremybuckgoes.request.RegisterRequest;
import kc.wheremybuckgoes.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;

    @Value("${application.register}")
    private boolean isRegistrationEnabled;

    public AuthenticationResponse register(RegisterRequest request) {
        if(!isRegistrationEnabled) {return AuthenticationResponse.builder().accessToken(null).accessToken(null).message("Registration of new members is not allowed.").code(HttpStatus.METHOD_NOT_ALLOWED).build();}
        User found = userRepo.findByEmail(request.getEmail()).orElse(null);
        if(found != null){
            return AuthenticationResponse.builder().accessToken(null).accessToken(null).message("User already exists").code(HttpStatus.CONFLICT).build();
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ApplicationConstant.Role.USER)
                .build();
        var savedUser = userRepo.save(user);
        var jwtToken = jwtService.generateToken(new HashMap<>(), user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).code(HttpStatus.OK).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(new HashMap<>(), user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).code(HttpStatus.OK).build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .tokenValue(jwtToken)
                .tokenType(ApplicationConstant.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.getUserEmailFromJWT(refreshToken);
        if (userEmail != null) {
            var user = userRepo.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(new HashMap<>(),user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}