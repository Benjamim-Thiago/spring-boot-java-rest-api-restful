package br.com.course.services;

import br.com.course.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.logging.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PersonServicesTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private PersonServices personServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Person> persons = personServices.findAll();
        assertEquals(8, persons.size());
        verify(logger, times(1)).info("Finding all people!");
    }

    @Test
    void testFindById() {
        String id = "1";
        Person person = personServices.findById(id);
        assertEquals(Long.parseLong(id), person.getId());
        assertEquals("Leandro", person.getFirstName());
        assertEquals("Costa", person.getLastName());
        assertEquals("Uberlândia - Minas Gerais - Brasil", person.getAddress());
        assertEquals("Male", person.getGender());
        verify(logger, times(1)).info("Finding one person!");
    }

    @Test
    void testCreate() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        Person createdPerson = personServices.create(person);
        assertEquals("John", createdPerson.getFirstName());
        assertEquals("Doe", createdPerson.getLastName());
        verify(logger, times(1)).info("Creating one person!");
    }

    @Test
    void testUpdate() {
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Doe");

        Person updatedPerson = personServices.update(person);
        assertEquals(1L, updatedPerson.getId());
        assertEquals("John", updatedPerson.getFirstName());
        assertEquals("Doe", updatedPerson.getLastName());
        verify(logger, times(1)).info("Updating one person!");
    }

    @Test
    void testDelete() {
        String id = "1";
        personServices.delete(id);
        verify(logger, times(1)).info("Deleting one person!");
    }
}
