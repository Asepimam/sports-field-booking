package org.sports.field.booking.interfaces.rest;

import java.util.List;

import org.sports.field.booking.application.dto.GroundRequestDTO;
import org.sports.field.booking.application.dto.GroundResponseDTO;
import org.sports.field.booking.interfaces.model.ApiResponse;
import org.sports.field.booking.interfaces.model.Meta;
import org.sports.field.booking.application.service.GroundService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@ApplicationScoped
@Path("/grounds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroundResource {
    private final GroundService groundService;

    public GroundResource(GroundService groundService) {
        this.groundService = groundService;
    }

    @POST
    @RolesAllowed("OWNER")
    public Response createGround(@Valid GroundRequestDTO request) {
        GroundResponseDTO ground = groundService.createGround(currentEmail(), request);

        return Response
                .status(Response.Status.CREATED)
                .entity(new ApiResponse<>("SUCCESS", ground))
                .build();
    }

    @GET
    @RolesAllowed({ "CUSTOMER", "OWNER" })
    public Response getGrounds() {
        int page = 1;
        int size = 10;

        List<GroundResponseDTO> grounds = groundService.getGrounds(page, size);
        long total = groundService.countGrounds();

        Meta meta = new Meta(page, size, total);
        return Response.ok(
                new ApiResponse<>("SUCCESS", grounds, meta)).build();
    }

    @GET
    @Path("/owner")
    @RolesAllowed("OWNER")
    public Response getOwnerGrounds() {
        int page = 1;
        int size = 10;

        List<GroundResponseDTO> grounds = groundService.getOwnerGrounds(currentEmail(), page, size);
        long total = groundService.countOwnerGrounds(currentEmail());

        Meta meta = new Meta(page, size, total);
        return Response.ok(new ApiResponse<>("SUCCESS", grounds, meta)).build();
    }

    @Context
    SecurityContext securityContext;

    private String currentEmail() {
        return securityContext.getUserPrincipal().getName();
    }

    @GET
    @Path("/public")
    @PermitAll
    public Response getPublicGrounds(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("order") @DefaultValue("DESC") String order) {

        List<GroundResponseDTO> grounds = groundService.getPublicGrounds(page, size, sortBy, order);
        long total = groundService.countPublicGrounds();

        Meta meta = new Meta(page, size, total);
        return Response.ok(new ApiResponse<>("SUCCESS", grounds, meta)).build();
    }

    @GET
    @Path("/public/featured")
    @PermitAll
    public Response getFeaturedGrounds(@QueryParam("limit") @DefaultValue("6") int limit) {
        List<GroundResponseDTO> featuredGrounds = groundService.getFeaturedGrounds(limit);
        return Response.ok(new ApiResponse<>("SUCCESS", featuredGrounds)).build();
    }

    @GET
    @Path("/public/search")
    @PermitAll
    public Response searchPublicGrounds(
            @QueryParam("keyword") String keyword,
            @QueryParam("location") String location,
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        // sportType diabaikan karena tidak ada di entity
        List<GroundResponseDTO> grounds = groundService.searchPublicGrounds(
                keyword, null, location, minPrice, maxPrice, page, size);

        long total = groundService.countSearchPublicGrounds(keyword, null, location, minPrice, maxPrice);

        Meta meta = new Meta(page, size, total);
        return Response.ok(new ApiResponse<>("SUCCESS", grounds, meta)).build();
    }
}
