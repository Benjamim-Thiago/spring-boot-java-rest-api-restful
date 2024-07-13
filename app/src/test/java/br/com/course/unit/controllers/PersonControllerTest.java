package br.com.course.unit.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import br.com.course.controllers.PersonController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.course.data.vo.v1.PersonVO;
import br.com.course.services.PersonServices;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonServices service;

    @Autowired
    private ObjectMapper objectMapper;

    private PersonVO personVO1;
    private PersonVO personVO2;

    @BeforeEach
    public void setUp() {
        personVO1 = new PersonVO();
        personVO1.setId(1L);
        personVO1.setFirstName("John");
        personVO1.setLastName("Doe");
        personVO1.setAddress("123 Main St");
        personVO1.setGender("Male");

        personVO2 = new PersonVO();
        personVO2.setId(2L);
        personVO2.setFirstName("Jane");
        personVO2.setLastName("Doe");
        personVO2.setAddress("456 Main St");
        personVO2.setGender("Female");
    }

    @Test
    public void testFindAll() throws Exception {
        List<PersonVO> persons = Arrays.asList(personVO1, personVO2);
        when(service.findAll()).thenReturn(persons);

        mockMvc.perform(get("/person")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(persons.size()))
                .andExpect(jsonPath("$[0].id").value(personVO1.getId()))
                .andExpect(jsonPath("$[0].firstName").value(personVO1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(personVO1.getLastName()))
                .andExpect(jsonPath("$[0].address").value(personVO1.getAddress()))
                .andExpect(jsonPath("$[0].gender").value(personVO1.getGender()))
                .andExpect(jsonPath("$[1].id").value(personVO2.getId()))
                .andExpect(jsonPath("$[1].firstName").value(personVO2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(personVO2.getLastName()))
                .andExpect(jsonPath("$[1].address").value(personVO2.getAddress()))
                .andExpect(jsonPath("$[1].gender").value(personVO2.getGender()));
    }

    @Test
    public void testFindById() throws Exception {
        when(service.findById(1L)).thenReturn(personVO1);

        mockMvc.perform(get("/person/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(personVO1.getId()))
                .andExpect(jsonPath("$.firstName").value(personVO1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(personVO1.getLastName()))
                .andExpect(jsonPath("$.address").value(personVO1.getAddress()))
                .andExpect(jsonPath("$.gender").value(personVO1.getGender()));
    }

    @Test
    public void testCreate() throws Exception {
        when(service.create(any(PersonVO.class))).thenReturn(personVO1);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personVO1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(personVO1.getId()))
                .andExpect(jsonPath("$.firstName").value(personVO1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(personVO1.getLastName()))
                .andExpect(jsonPath("$.address").value(personVO1.getAddress()))
                .andExpect(jsonPath("$.gender").value(personVO1.getGender()));
    }

    @Test
    public void testUpdate() throws Exception {
        when(service.update(any(PersonVO.class))).thenReturn(personVO1);

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personVO1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(personVO1.getId()))
                .andExpect(jsonPath("$.firstName").value(personVO1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(personVO1.getLastName()))
                .andExpect(jsonPath("$.address").value(personVO1.getAddress()))
                .andExpect(jsonPath("$.gender").value(personVO1.getGender()));
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/person/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
