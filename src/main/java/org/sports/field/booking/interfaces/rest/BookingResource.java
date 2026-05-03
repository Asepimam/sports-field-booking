package org.sports.field.booking.interfaces.rest;

import java.util.List;

import org.sports.field.booking.application.dto.BookingRequestDTO;
import org.sports.field.booking.application.dto.BookingResponseDTO;
import org.sports.field.booking.application.service.BookingService;
import org.sports.field.booking.interfaces.model.ApiResponse;
import org.sports.field.booking.interfaces.model.Meta;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {
    private final BookingService bookingService;

    @Context
    SecurityContext securityContext;

    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @POST
    @RolesAllowed("CUSTOMER")
    public Response createBooking(@Valid BookingRequestDTO request) {
        BookingResponseDTO booking = bookingService.createBooking(currentEmail(), request);
        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse<>("SUCCESS", booking))
                .build();
    }

    @GET
    @RolesAllowed("CUSTOMER")
    public Response getMyBookings() {
        int page = 1;
        int size = 10;

        List<BookingResponseDTO> bookings = bookingService.getMyBookings(currentEmail(), page, size);
        long total = bookingService.countMyBookings(currentEmail());

        return Response.ok(new ApiResponse<>("SUCCESS", bookings, new Meta(page, size, total))).build();
    }

    @GET
    @Path("/owner")
    @RolesAllowed("OWNER")
    public Response getOwnerBookings() {
        int page = 1;
        int size = 10;

        List<BookingResponseDTO> bookings = bookingService.getOwnerBookings(currentEmail(), page, size);
        long total = bookingService.countOwnerBookings(currentEmail());

        return Response.ok(new ApiResponse<>("SUCCESS", bookings, new Meta(page, size, total))).build();
    }

    private String currentEmail() {
        return securityContext.getUserPrincipal().getName();
    }
}
