package org.sports.field.booking.resource;

import org.sports.field.booking.dto.CheckEmailRequestDTO;
import org.sports.field.booking.dto.EmailCheckResponse;
import org.sports.field.booking.dto.LoginRequestDTO;

import org.sports.field.booking.dto.RefreshTokenRequestDTO;
import org.sports.field.booking.model.ApiResponse;
import org.sports.field.booking.service.AuthService;

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

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequestDTO request) {
        try {
            var response = authService.login(request);
            return Response.ok(new ApiResponse<>("SUCCESS", response)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse<>("FAILURE", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(@Valid RefreshTokenRequestDTO request) {
        try {
            var response = authService.refreshToken(request);
            return Response.ok(new ApiResponse<>("SUCCESS", response)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse<>("FAILURE", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/validate")
    public Response validateToken(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            // Extract token from Bearer header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse<>("FAILURE", "Invalid or missing authorization header"))
                        .build();
            }

            String token = authorizationHeader.substring(7);
            boolean isValid = authService.validateToken(token);

            if (isValid) {
                return Response.ok(new ApiResponse<>("SUCCESS", "Token is valid")).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse<>("FAILURE", "Token is invalid"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse<>("FAILURE", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            // Extract token from Bearer header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse<>("FAILURE", "Invalid or missing authorization header"))
                        .build();
            }

            String token = authorizationHeader.substring(7);
            authService.logout(token);

            return Response.ok(new ApiResponse<>("SUCCESS", "Logged out successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse<>("FAILURE", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/me")
    public Response getCurrentUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            // Extract token from Bearer header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse<>("FAILURE", "Invalid or missing authorization header"))
                        .build();
            }

            String token = authorizationHeader.substring(7);
            var user = authService.getCurrentUser(token);

            return Response.ok(new ApiResponse<>("SUCCESS", user)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse<>("FAILURE", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/check-email")
    public Response checkEmail(@Valid CheckEmailRequestDTO request) {
        try {
            boolean exists = authService.checkEmailExists(request.getEmail());
            return Response.ok(new ApiResponse<>("SUCCESS",
                    new EmailCheckResponse(exists, exists ? "Email already exists" : "Email is available")))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse<>("FAILURE", e.getMessage()))
                    .build();
        }
    }
}