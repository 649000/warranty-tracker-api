package com.nazri.resource;

import com.nazri.model.Company;
import com.nazri.service.CompanyService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource extends BaseResource {

    @Inject
    CompanyService companyService;

    @Inject
    JsonWebToken jwt;

    @Context
    SecurityContext securityContext;

    @GET
    public Response getAllCompanies() {
        try {
            List<Company> companies = Company.listAll();
            return Response.ok(companies).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving companies: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCompanyById(@PathParam("id") Long id) {
        try {
            Company company = Company.findById(id);
            if (company != null) {
                return Response.ok(company).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Company not found with id: " + id, "COMPANY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/search")
    public Response searchCompanies(@QueryParam("name") String name) {
        try {
            List<Company> companies = companyService.findByNameContaining(name);
            return Response.ok(companies).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error searching companies: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @POST
    @RolesAllowed("admin")
    public Response createCompany(Company company) {
        try {
            // Check if company with same name already exists
            if (company.getName() != null && !company.getName().isEmpty()) {
                List<Company> existingCompanies = Company.find("name", company.getName()).list();
                if (!existingCompanies.isEmpty()) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity(createErrorResponse("Company with this name already exists", "COMPANY_EXISTS", Response.Status.CONFLICT.getStatusCode()))
                            .build();
                }
            }

            company = companyService.updateCompany(company);
            return Response.status(Response.Status.CREATED).entity(company).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error creating company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updateCompany(@PathParam("id") Long id, Company company) {
        try {
            Company existingCompany = Company.findById(id);
            if (existingCompany != null) {
                // Set the ID to ensure we're updating the correct company
                company.setId(id);
                company = companyService.updateCompany(company);
                return Response.ok(company).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Company not found with id: " + id, "COMPANY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteCompany(@PathParam("id") Long id) {
        try {
            Company company = Company.findById(id);
            if (company != null) {
                companyService.deleteCompany(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Company not found with id: " + id, "COMPANY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error deleting company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }
}
