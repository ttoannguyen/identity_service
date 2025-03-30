package com.toan.identity_service.controller;

import com.nimbusds.jose.JOSEException;
import com.toan.identity_service.Service.AuthenticationService;
import com.toan.identity_service.dto.request.ApiResponse;
import com.toan.identity_service.dto.request.AuthenticationRequest;
import com.toan.identity_service.dto.request.IntrospectRequest;
import com.toan.identity_service.dto.response.AuthenticationResponse;
import com.toan.identity_service.dto.response.IntrospectResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticate(authenticationRequest);
        return ApiResponse
                .<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest authenticationRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(authenticationRequest);

        return ApiResponse
                .<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
