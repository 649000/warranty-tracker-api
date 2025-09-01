package com.nazri.resource;

import com.nazri.model.Company;
import com.nazri.service.CompanyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource extends BaseResource {

    private static final Logger LOG = Logger.getLogger(CompanyResource.class);

    @Inject
    CompanyService companyService;

    @GET
    public Response getAllCompanies() {
        try {
            LOG.info("Fetching all companies");
            List<Company> companies = companyService.findAllCompanies();
            LOG.info("Found " + companies.size() + " companies");

            return Response.ok(companies).build();
        } catch (Exception e) {
            LOG.error("Error retrieving companies", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving companies: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCompanyById(@PathParam("id") Long id) {
        try {
            LOG.info("Fetching company by id: " + id);
            return companyService.findById(id)
                    .map(company -> Response.ok(company).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(createErrorResponse("Company not found with id: " + id, "COMPANY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                            .build());
        } catch (Exception e) {
            LOG.error("Error retrieving company by id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchCompanies(@QueryParam("name") String name) {
        try {
            LOG.info("Searching companies by name: " + name);
            List<Company> companies;
            if (name == null || name.trim().isEmpty()) {
                companies = companyService.findAllCompanies();
            } else {
                companies = companyService.findByNameContaining(name);
            }

            return Response.ok(companies).build();
        } catch (Exception e) {
            LOG.error("Error searching companies by name: " + name, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error searching companies: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @POST
    public Response createCompany(Company company) {
        try {
            LOG.info("Creating company: " + company.getName());
            Company createdCompany = companyService.createCompany(company);
            return Response.status(Response.Status.CREATED).entity(createdCompany).build();
        } catch (IllegalArgumentException e) {
            LOG.warn("Validation error creating company: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage(), "VALIDATION_ERROR", Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        } catch (Exception e) {
            LOG.error("Error creating company: " + company.getName(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error creating company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateCompany(@PathParam("id") Long id, Company company) {
        try {
            LOG.info("Updating company with id: " + id);
            return companyService.updateCompany(id, company)
                    .map(updatedCompany -> Response.ok(updatedCompany).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(createErrorResponse("Company not found with id: " + id, "COMPANY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                            .build());
        } catch (Exception e) {
            LOG.error("Error updating company with id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCompany(@PathParam("id") Long id) {
        try {
            LOG.info("Deleting company with id: " + id);
            if (companyService.deleteCompany(id)) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Company not found with id: " + id, "COMPANY_NOT_FOUND", Response.Status.NOT_FOUND.getStatusCode()))
                        .build();
            }
        } catch (Exception e) {
            LOG.error("Error deleting company with id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error deleting company: " + e.getMessage(), "INTERNAL_ERROR", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }
}
