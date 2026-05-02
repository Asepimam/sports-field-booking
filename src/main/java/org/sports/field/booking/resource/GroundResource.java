package org.sports.field.booking.resource;

import java.util.List;

import org.sports.field.booking.dto.GroundRequestDTO;
import org.sports.field.booking.dto.GroundResponseDTO;
import org.sports.field.booking.model.ApiResponse;
import org.sports.field.booking.model.Meta;
import org.sports.field.booking.service.GroundService;

import jakarta.annotation.security.RolesAllowed;
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
@Path("grounds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "User", "Admin" })
public class GroundResource {
    private final GroundService groundService;

    public GroundResource(GroundService groundService) {
        this.groundService = groundService;
    }

    @POST
    public Response createGround(@Valid GroundRequestDTO request) {
        GroundResponseDTO ground = groundService.createGround(request);

        return Response
                .status(Response.Status.CREATED)
                .entity(new ApiResponse<>("SUCCESS", ground))
                .build();
    }

    @GET
    public Response getGrounds() {
        int page = 1;
        int size = 10;

        List<GroundResponseDTO> grounds = groundService.getGrounds(page, size);
        long total = groundService.countGrounds();

        Meta meta = new Meta(page, size, total);
        return Response.ok(
                new ApiResponse<>("SUCCESS", grounds, meta)).build();
    }

}
