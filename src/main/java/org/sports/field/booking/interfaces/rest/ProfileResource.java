package org.sports.field.booking.interfaces.rest;

import org.slf4j.Logger;
import org.sports.field.booking.application.dto.UpdateUserDTO;
import org.sports.field.booking.application.service.ProfileService;
import org.sports.field.booking.infrastructure.security.JsonWebToken;
import org.sports.field.booking.infrastructure.security.JwtService;
import org.sports.field.booking.interfaces.model.ApiResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/profile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "CUSTOMER", "OWNER" })
public class ProfileResource {
    private final ProfileService profileService;
    private final JwtService jwtService;
    @Context
    SecurityContext securityContext;

    public ProfileResource(ProfileService profileService, JwtService jwtService) {
        this.profileService = profileService;
        this.jwtService = jwtService;
    }

    @GET
    @Path("me")
    public Response getProfile() {
        return Response.ok(new ApiResponse<>("SUCCESS", profileService.getProfile(currentEmail()))).build();
    }

    @PUT
    public Response updateProfile(@Valid UpdateUserDTO request) {
        return Response.ok(new ApiResponse<>("SUCCESS", profileService.updateProfile(currentEmail(), request))).build();
    }

    private String currentEmail() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new WebApplicationException("User not authenticated", Response.Status.UNAUTHORIZED);
        }

        String principalName = securityContext.getUserPrincipal().getName();

        return principalName;
    }
}
