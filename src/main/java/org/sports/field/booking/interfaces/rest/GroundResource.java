package org.sports.field.booking.interfaces.rest;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

import org.sports.field.booking.application.dto.GroundRequestDTO;
import org.sports.field.booking.application.dto.GroundResponseDTO;
import org.sports.field.booking.interfaces.model.ApiResponse;
import org.sports.field.booking.interfaces.model.Meta;
import org.sports.field.booking.application.service.BookingService;
import org.sports.field.booking.application.service.GroundService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PUT;
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
    private final BookingService bookingService;

    public GroundResource(GroundService groundService, BookingService bookingService) {
        this.groundService = groundService;
        this.bookingService = bookingService;
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

    @PUT
    @Path("/{id}")
    @RolesAllowed("OWNER")
    public Response updateGround(@PathParam("id") UUID id, @Valid GroundRequestDTO request) {
        GroundResponseDTO ground = groundService.updateGround(currentEmail(), id, request);

        return Response.ok(new ApiResponse<>("SUCCESS", ground)).build();
    }

    @GET
    @RolesAllowed({ "CUSTOMER", "OWNER" })
    public Response getGrounds(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("q") String keyword,
            @QueryParam("sport") String sport,
            @QueryParam("location") String location,
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice) {
        List<GroundResponseDTO> grounds;
        long total;

        if (hasSearchFilters(keyword, sport, location, minPrice, maxPrice)) {
            grounds = groundService.searchPublicGrounds(keyword, sport, location, minPrice, maxPrice, page, size);
            total = groundService.countSearchPublicGrounds(keyword, sport, location, minPrice, maxPrice);
        } else {
            grounds = groundService.getGrounds(page, size);
            total = groundService.countGrounds();
        }

        Meta meta = new Meta(page, size, total);
        return Response.ok(
                new ApiResponse<>("SUCCESS", grounds, meta)).build();
    }

    @GET
    @Path("/locations")
    @RolesAllowed({ "CUSTOMER", "OWNER" })
    public Response getGroundLocations(
            @QueryParam("q") String keyword,
            @QueryParam("limit") @DefaultValue("20") int limit) {
        List<String> locations = groundService.getPublicLocations(keyword, limit);
        return Response.ok(new ApiResponse<>("SUCCESS", locations)).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getGroundById(@PathParam("id") UUID id) {
        GroundResponseDTO ground = groundService.getGroundById(id);

        return Response.ok(new ApiResponse<>("SUCCESS", ground)).build();
    }

    @GET
    @Path("/{id}/available-slots")
    @PermitAll
    public Response getAvailableSlots(@PathParam("id") UUID id, @QueryParam("date") LocalDate date) {
        List<String> availableSlots = bookingService.getAvailableSlots(id, date);

        return Response.ok(new ApiResponse<>("SUCCESS", availableSlots)).build();
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
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("order") @DefaultValue("DESC") String order,
            @QueryParam("q") String keyword,
            @QueryParam("sport") String sport,
            @QueryParam("location") String location,
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice) {
        int page = 1;
        int size = 10;
        List<GroundResponseDTO> grounds;
        long total;

        if (hasSearchFilters(keyword, sport, location, minPrice, maxPrice)) {
            grounds = groundService.searchPublicGrounds(keyword, sport, location, minPrice, maxPrice, page, size);
            total = groundService.countSearchPublicGrounds(keyword, sport, location, minPrice, maxPrice);
        } else {
            grounds = groundService.getPublicGrounds(page, size, sortBy, order);
            total = groundService.countPublicGrounds();
        }

        Meta meta = new Meta(page, size, total);
        return Response.ok(new ApiResponse<>("SUCCESS", grounds, meta)).build();
    }

    @GET
    @Path("/public/locations")
    @PermitAll
    public Response getPublicLocations(
            @QueryParam("q") String keyword,
            @QueryParam("limit") @DefaultValue("10") int limit) {
        List<String> locations = groundService.getPublicLocations(keyword, Math.min(limit, 10));
        return Response.ok(new ApiResponse<>("SUCCESS", locations)).build();
    }

    @GET
    @Path("/public/featured")
    @PermitAll
    public Response getFeaturedGrounds(@QueryParam("limit") @DefaultValue("6") int limit) {
        List<GroundResponseDTO> featuredGrounds = groundService.getFeaturedGrounds(limit);
        return Response.ok(new ApiResponse<>("SUCCESS", featuredGrounds)).build();
    }

    private boolean hasSearchFilters(String keyword, String sport, String location, Double minPrice, Double maxPrice) {
        return isFilled(keyword)
                || isFilled(sport)
                || isFilled(location)
                || minPrice != null
                || maxPrice != null;
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
