package org.sports.field.booking.resource;

import java.util.List;

import org.sports.field.booking.dto.UserRequestDTO;
import org.sports.field.booking.dto.UserResponseDTO;
import org.sports.field.booking.model.ApiResponse;
import org.sports.field.booking.model.Meta;
import org.sports.field.booking.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    public Response createUser(@Valid UserRequestDTO request) {

        UserResponseDTO user = userService.createUser(request);

        return Response
                .status(Response.Status.CREATED)
                .entity(new ApiResponse<>("SUCCESS", user))
                .build();
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