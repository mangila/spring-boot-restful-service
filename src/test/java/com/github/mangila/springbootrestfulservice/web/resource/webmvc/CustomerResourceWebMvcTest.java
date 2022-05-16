package com.github.mangila.springbootrestfulservice.web.resource.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.springbootrestfulservice.service.CustomerService;
import com.github.mangila.springbootrestfulservice.web.dto.CustomerDto;
import com.github.mangila.springbootrestfulservice.web.resource.CustomerResource;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_LOCATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("webmvc")
@WebMvcTest(CustomerResource.class)
class CustomerResourceWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService service;

    @Test
    void findAll() throws Exception {
        when(this.service.findAll()).thenReturn(Lists.newArrayList(new CustomerDto()));

        this.mockMvc.perform(get("/api/v1/customer").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findById() throws Exception {
        String uuid = UUID.randomUUID().toString();
        when(this.service.findById(uuid)).thenReturn(new CustomerDto());

        this.mockMvc.perform(get("/api/v1/customer/" + uuid)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":null,\"name\":null,\"registration\":null,\"orderHistory\":null}"));

    }

    @Test
    void insertNewCustomer() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Frej");
        String uuid = UUID.randomUUID().toString();
        when(this.service.insert(customerDto)).thenReturn(uuid);

        this.mockMvc.perform(post("/api/v1/customer")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/api/v1/customer/" + uuid));
    }

    @Test
    void updateCustomer() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Frigg");
        String uuid = UUID.randomUUID().toString();
        when(this.service.update(uuid, customerDto)).thenReturn(uuid);

        this.mockMvc.perform(put("/api/v1/customer/" + uuid)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(customerDto)))
                .andExpect(status().isNoContent())
                .andExpect(header().string(CONTENT_LOCATION, "/api/v1/customer/" + uuid));
    }

    @Test
    void deleteById() throws Exception {
        String uuid = UUID.randomUUID().toString();
        this.mockMvc.perform(delete("/api/v1/customer/" + uuid)
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":" + uuid + "}"))
                .andExpect(status().isNoContent());
    }
}