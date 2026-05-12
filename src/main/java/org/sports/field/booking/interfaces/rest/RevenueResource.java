package org.sports.field.booking.interfaces.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.sports.field.booking.application.service.RevenueService;
import org.sports.field.booking.interfaces.model.ApiResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/revenue")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RevenueResource {
    private final RevenueService revenueService;

    @Context
    SecurityContext securityContext;

    public RevenueResource(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GET
    @Path("/owner")
    @RolesAllowed("OWNER")
    public Response getOwnerRevenue(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {

        LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now().minusMonths(1);

        LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now();

        Map<String, Object> revenue = revenueService.getOwnerRevenue(currentEmail(), startDate, endDate);
        return Response.ok(new ApiResponse<>("SUCCESS", revenue)).build();
    }

    @GET
    @Path("/owner/summary")
    @RolesAllowed("OWNER")
    public Response getOwnerRevenueSummary() {
        Map<String, Object> summary = revenueService.getOwnerRevenueSummary(currentEmail());
        return Response.ok(new ApiResponse<>("SUCCESS", summary)).build();
    }

    private String currentEmail() {
        return securityContext.getUserPrincipal().getName();
    }
}
