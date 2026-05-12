package org.sports.field.booking.interfaces.rest;

import java.util.List;
import java.util.UUID;

import org.sports.field.booking.application.dto.FacilityDTO;
import org.sports.field.booking.application.service.FacilityService;
import org.sports.field.booking.interfaces.model.ApiResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/grounds/{groundId}/facilities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FacilityResource {
    private final FacilityService facilityService;

    @Context
    SecurityContext securityContext;

    public FacilityResource(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @POST
    @RolesAllowed("OWNER")
    public Response createFacility(@PathParam("groundId") UUID groundId, @Valid FacilityDTO request) {
        FacilityDTO facility = facilityService.createFacility(currentEmail(), groundId, request);
        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse<>("SUCCESS", facility))
                .build();
    }

    @PUT
    @Path("/{facilityId}")
    @RolesAllowed("OWNER")
    public Response updateFacility(
            @PathParam("groundId") UUID groundId,
            @PathParam("facilityId") UUID facilityId,
            @Valid FacilityDTO request) {
        FacilityDTO facility = facilityService.updateFacility(currentEmail(), groundId, facilityId, request);
        return Response.ok(new ApiResponse<>("SUCCESS", facility)).build();
    }

    @DELETE
    @Path("/{facilityId}")
    @RolesAllowed("OWNER")
    public Response deleteFacility(
            @PathParam("groundId") UUID groundId,
            @PathParam("facilityId") UUID facilityId) {
        facilityService.deleteFacility(currentEmail(), groundId, facilityId);
        return Response.ok(new ApiResponse<>("SUCCESS", "Facility deleted successfully")).build();
    }

    @GET
    @RolesAllowed({ "CUSTOMER", "OWNER" })
    public Response getGroundFacilities(@PathParam("groundId") UUID groundId) {
        List<FacilityDTO> facilities = facilityService.getGroundFacilities(groundId);
        return Response.ok(new ApiResponse<>("SUCCESS", facilities)).build();
    }

    private String currentEmail() {
        return securityContext.getUserPrincipal().getName();
    }
}
