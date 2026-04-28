package org.sports.field.booking.resource;

import org.sports.field.booking.service.GroundService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("Grounds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroundResource {
    private final GroundService groundService;

    public GroundResource(GroundService groundService) {
        this.groundService = groundService;
    }

}
