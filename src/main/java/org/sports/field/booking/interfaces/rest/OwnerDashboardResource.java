package org.sports.field.booking.interfaces.rest;

import org.sports.field.booking.application.service.OwnerDashboardService;
import org.sports.field.booking.interfaces.model.ApiResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/owner/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("OWNER")
public class OwnerDashboardResource {
    private final OwnerDashboardService ownerDashboardService;

    @Context
    SecurityContext securityContext;

    public OwnerDashboardResource(OwnerDashboardService ownerDashboardService) {
        this.ownerDashboardService = ownerDashboardService;
    }

    @GET
    public Response getDashboard() {
        return Response.ok(new ApiResponse<>("SUCCESS", ownerDashboardService.getDashboard(currentEmail()))).build();
    }

    private String currentEmail() {
        return securityContext.getUserPrincipal().getName();
    }
}
