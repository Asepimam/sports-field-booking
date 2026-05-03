package org.sports.field.booking.interfaces.rest;

import org.sports.field.booking.application.dto.CheckEmailRequestDTO;
import org.sports.field.booking.application.dto.EmailCheckResponse;
import org.sports.field.booking.application.dto.LoginRequestDTO;

import org.sports.field.booking.application.dto.RefreshTokenRequestDTO;
import org.sports.field.booking.application.dto.UserRequestDTO;
import org.sports.field.booking.application.dto.UserResponseDTO;
import org.sports.field.booking.application.exception.InputException;
import org.sports.field.booking.interfaces.model.ApiResponse;
import org.sports.field.booking.application.service.AuthService;
import org.sports.field.booking.application.service.UserService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    SecurityContext securityContext;

    @Inject
    UserService userService;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequestDTO request) {
        var response = authService.login(request);
        return Response.ok(new ApiResponse<>("SUCCESS", response)).build();
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response createUser(@Valid UserRequestDTO request) {

        UserResponseDTO user = userService.createUser(request);

        return Response
                .status(Response.Status.CREATED)
                .entity(new ApiResponse<>("SUCCESS", user))
                .build();
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public Response refreshToken(@Valid RefreshTokenRequestDTO request) {
        var response = authService.refreshToken(request);
        return Response.ok(new ApiResponse<>("SUCCESS", response)).build();
    }

    @POST
    @Path("/validate")
    @RolesAllowed({ "CUSTOMER", "OWNER" })
    public Response validateToken(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        boolean isValid = authService.validateToken(token);

        if (!isValid) {
            throw new InputException("Token is invalid");
        }

        return Response.ok(new ApiResponse<>("SUCCESS", "Token is valid")).build();
    }

    @POST
    @Path("/logout")
    @RolesAllowed({ "CUSTOMER", "OWNER" })
    public Response logout(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        authService.logout(token);

        return Response.ok(new ApiResponse<>("SUCCESS", "Logged out successfully")).build();
    }

    @POST
    @Path("/me")
    @RolesAllowed({ "CUSTOMER", "OWNER" })
    public Response getCurrentUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        var user = authService.getCurrentUser(token);

        return Response.ok(new ApiResponse<>("SUCCESS", user)).build();
    }

    @POST
    @Path("/check-email")
    @PermitAll
    public Response checkEmail(@Valid CheckEmailRequestDTO request) {
        boolean exists = authService.checkEmailExists(request.getEmail());
        return Response.ok(new ApiResponse<>("SUCCESS",
                new EmailCheckResponse(exists, exists ? "Email already exists" : "Email is available")))
                .build();
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new InputException("Invalid or missing authorization header");
        }

        return authorizationHeader.substring(7);
    }
}
