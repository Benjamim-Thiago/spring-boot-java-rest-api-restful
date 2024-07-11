package br.com.course.controllers;

import br.com.course.model.Person;
import br.com.course.services.PersonServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PersonControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PersonServices service;

    @InjectMocks
    private PersonController controller;

    private static final String BASE_URL = "/person";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testFindAll() throws Exception {
        List<Person> persons = Arrays.asList(createPerson(1L, "John", "Doe", "123 Street", "Male"),
                createPerson(2L, "Jane", "Doe", "456 Avenue", "Female"));

        when(service.findAll()).thenReturn(persons);

        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].address").value("123 Street"))
                .andExpect(jsonPath("$[0].gender").value("Male"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].address").value("456 Avenue"))
                .andExpect(jsonPath("$[1].gender").value("Female"));
    }

    @Test
    void testFindById() throws Exception {
        Long id = 1L;
        Person person = createPerson(id, "John", "Doe", "123 Street", "Male");

        when(service.findById(id)).thenReturn(person);

        mockMvc.perform(get(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("123 Street"))
                .andExpect(jsonPath("$.gender").value("Male"));
    }

    @Test
    void testCreate() throws Exception {
        Person person = createPerson(null, "John", "Doe", "123 Street", "Male");
        Person personSaved = createPerson(1L, "John", "Doe", "123 Street", "Male");

        when(service.create(any(Person.class))).thenReturn(personSaved);
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("123 Street"))
                .andExpect(jsonPath("$.gender").value("Male"));
    }

    @Test
    void testUpdate() throws Exception {
        Long id = 1L;
        Person person = createPerson(id, "John", "Doe", "123 Street", "Male");

        when(service.update(any(Person.class))).thenReturn(person);

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("123 Street"))
                .andExpect(jsonPath("$.gender").value("Male"));
    }

    @Test
    void testDelete() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(id);
    }

    private Person createPerson(Long id, String firstName, String lastName, String address, String gender) {
        Person person = new Person();
        person.setId(id);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAddress(address);
        person.setGender(gender);
        return person;
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
