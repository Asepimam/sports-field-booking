package org.sports.field.booking.interfaces.rest;

import java.util.List;

import org.sports.field.booking.application.dto.UserRequestDTO;
import org.sports.field.booking.application.dto.UserResponseDTO;
import org.sports.field.booking.interfaces.model.ApiResponse;
import org.sports.field.booking.interfaces.model.Meta;
import org.sports.field.booking.application.service.UserService;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "CUSTOMER", "OWNER" })
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    public Response getUsers() {
        int page = 1;
        int size = 10;

        List<UserResponseDTO> users = userService.getUsers(page, size);
        long total = userService.countUsers();

        Meta meta = new Meta(page, size, total);

        return Response.ok(
                new ApiResponse<>("SUCCESS", users, meta)).build();
    }
}
