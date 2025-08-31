package com.nazri.resource;

import com.nazri.model.Claim;
import com.nazri.service.ClaimService;
import com.nazri.service.UserService;
import com.nazri.model.User;
import com.nazri.model.Warranty;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/claims")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClaimResource extends BaseResource {

    @