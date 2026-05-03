package org.sports.field.booking.interfaces.rest;

import org.sports.field.booking.application.dto.UpdateUserDTO;
import org.sports.field.booking.application.service.ProfileService;
import org.sports.field.booking.interfaces.model.ApiResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

    @Context
    SecurityContext securityContext;

    public ProfileResource(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GET
    public Response getProfile() {
        return Response.ok(new ApiResponse<>("SUCCESS", profileService.getProfile(currentEmail()))).build();
    }

    @PUT
    public Response updateProfile(@Valid UpdateUserDTO request) {
        return Response.ok(new ApiResponse<>("SUCCESS", profileService.updateProfile(currentEmail(), request))).build();
    }

    private String currentEmail() {
        return securityContext.getUserPrincipal().getName();
    }
}
