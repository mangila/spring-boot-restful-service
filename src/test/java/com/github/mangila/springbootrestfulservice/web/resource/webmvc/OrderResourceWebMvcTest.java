package com.github.mangila.springbootrestfulservice.web.resource.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.springbootrestfulservice.persistence.domain.Address;
import com.github.mangila.springbootrestfulservice.service.OrderService;
import com.github.mangila.springbootrestfulservice.web.dto.OrderDto;
import com.github.mangila.springbootrestfulservice.web.resource.OrderResource;
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
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("webmvc")
@WebMvcTest(OrderResource.class)
class OrderResourceWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

    @Test
    void findAll() throws Exception {
        when(this.service.findAll()).thenReturn(Lists.newArrayList(new OrderDto()));

        this.mockMvc.perform(get("/api/v1/order").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findById() throws Exception {
        String uuid = UUID.randomUUID().toString();
        when(this.service.findById(uuid)).thenReturn(new OrderDto());

        this.mockMvc.perform(get("/api/v1/order/" + uuid)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(
                        content()
                                .json(
                                        "{\"id\":null," +
                                                "\"products\":null," +
                                                "\"amount\":null," +
                                                "\"address\":null}",
                                        true));
    }

    @Test
    void insertNewOrder() throws Exception {
        OrderDto orderDto = new OrderDto();
        orderDto.setAddress(new Address());
        orderDto.setAmount(2000);
        orderDto.setProducts(Lists.newArrayList());
        String customerUUID = UUID.randomUUID().toString();
        String orderUUID = UUID.randomUUID().toString();
        when(this.service.insert(customerUUID, orderDto)).thenReturn(orderUUID);

        this.mockMvc.perform(post("/api/v1/order/" + customerUUID)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/api/v1/order/" + orderUUID));
    }

    @Test
    void deleteById() throws Exception {
        String uuid = UUID.randomUUID().toString();
        this.mockMvc.perform(delete("/api/v1/order/" + uuid)
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":" + uuid + "}"))
                .andExpect(status().isNoContent());
    }
}