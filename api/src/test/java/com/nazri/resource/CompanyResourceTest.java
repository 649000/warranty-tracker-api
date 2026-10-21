package com.nazri.resource;

import com.nazri.model.Company;
import com.nazri.service.CompanyService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyResourceTest {

    @Mock
    CompanyService companyService;

    @InjectMocks
    CompanyResource companyResource;

    private Company sampleCompany;

    @BeforeEach
    void setUp() {
        sampleCompany = new Company();
        sampleCompany.id = 1L;
        sampleCompany.setName("Samsung");
    }

    @Test
    void testGetAllCompanies() {
        when(companyService.findAllCompanies()).thenReturn(List.of(sampleCompany));

        Response response = companyResource.getAllCompanies();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(sampleCompany), response.getEntity());
    }

    @Test
    void testGetCompanyById_Found() {
        when(companyService.findById(1L)).thenReturn(Optional.of(sampleCompany));

        Response response = companyResource.getCompanyById(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetCompanyById_NotFound() {
        when(companyService.findById(999L)).thenReturn(Optional.empty());

        Response response = companyResource.getCompanyById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testSearchCompanies() {
        when(companyService.findByNameContaining("Sam")).thenReturn(List.of(sampleCompany));

        Response response = companyResource.searchCompanies("Sam");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminCreateCompany_Success() {
        when(companyService.createCompany(any(Company.class))).thenReturn(sampleCompany);

        Response response = companyResource.adminCreateCompany(sampleCompany);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(sampleCompany, response.getEntity());
    }

    @Test
    void testAdminCreateCompany_ValidationError() {
        when(companyService.createCompany(any(Company.class)))
                .thenThrow(new IllegalArgumentException("Company with this name already exists"));

        Response response = companyResource.adminCreateCompany(sampleCompany);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminUpdateCompany_Success() {
        when(companyService.updateCompany(eq(1L), any(Company.class)))
                .thenReturn(Optional.of(sampleCompany));

        Response response = companyResource.adminUpdateCompany(1L, sampleCompany);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminUpdateCompany_NotFound() {
        when(companyService.updateCompany(eq(999L), any(Company.class)))
                .thenReturn(Optional.empty());

        Response response = companyResource.adminUpdateCompany(999L, sampleCompany);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminDeleteCompany_Success() {
        when(companyService.deleteCompany(1L)).thenReturn(true);

        Response response = companyResource.adminDeleteCompany(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testAdminDeleteCompany_NotFound() {
        when(companyService.deleteCompany(999L)).thenReturn(false);

        Response response = companyResource.adminDeleteCompany(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testExceptionHandling500() {
        when(companyService.findAllCompanies()).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), companyResource.getAllCompanies().getStatus());

        when(companyService.findById(1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), companyResource.getCompanyById(1L).getStatus());

        when(companyService.findByNameContaining(any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), companyResource.searchCompanies("err").getStatus());

        when(companyService.createCompany(any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), companyResource.adminCreateCompany(sampleCompany).getStatus());

        when(companyService.updateCompany(eq(1L), any())).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), companyResource.adminUpdateCompany(1L, sampleCompany).getStatus());

        when(companyService.deleteCompany(1L)).thenThrow(new RuntimeException("Crash"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), companyResource.adminDeleteCompany(1L).getStatus());
    }
}
