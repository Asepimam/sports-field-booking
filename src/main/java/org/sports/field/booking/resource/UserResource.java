package org.sports.field.booking.resource;

import java.util.List;

import org.sports.field.booking.dto.UserRequestDTO;
import org.sports.field.booking.dto.UserResponseDTO;
import org.sports.field.booking.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    UserService userService;

    public UserResource(@Valid UserService userService) {
        this.userService = userService;
    }

    @POST
    public Response createUser(@Valid UserRequestDTO request) {
        UserResponseDTO response = userService.createUser(request);
        return Response.ok(response).build();
    }

    @GET
    public Response getUsers() {
        List<UserResponseDTO> users = userService.getAllUser();
        return Response.ok(users).build();
    }
}