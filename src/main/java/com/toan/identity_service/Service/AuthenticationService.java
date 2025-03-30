package com.toan.identity_service.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.toan.identity_service.dto.request.AuthenticationRequest;
import com.toan.identity_service.dto.request.IntrospectRequest;
import com.toan.identity_service.dto.response.AuthenticationResponse;
import com.toan.identity_service.dto.response.IntrospectResponse;
import com.toan.identity_service.entity.User;
import com.toan.identity_service.exception.AppException;
import com.toan.identity_service.exception.ErrorCode;
import com.toan.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        var token = introspectRequest.getToken();
        boolean isValid = false;

        try {
            // Parse the token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify token signature
            JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean signatureValid = signedJWT.verify(jwsVerifier);

            if (!signatureValid) {
                log.warn("Invalid token signature");
                return IntrospectResponse.builder()
                        .valid(false)
                        .build();
            }

            // Check expiration
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean notExpired = expiryTime != null && expiryTime.after(new Date());

            isValid = signatureValid && notExpired;

            if (!notExpired) {
                log.warn("Token has expired");
            }

        } catch (ParseException e) {
            log.error("Invalid token format", e);
            isValid = false;
        } catch (JOSEException e) {
            log.error("Error verifying token", e);
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticate = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticate) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("toanNg")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(4, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);


        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}
