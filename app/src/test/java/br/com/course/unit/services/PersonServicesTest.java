package br.com.course.unit.services;

import br.com.course.Validation.EntityNotExistException;
import br.com.course.data.vo.v1.model.PersonVO;
import br.com.course.model.Person;
import br.com.course.repositories.PersonRepository;
import br.com.course.services.PersonServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServicesTest {

    @InjectMocks
    private PersonServices service;

    @Mock
    private PersonRepository repository;

    private Person person1;
    private Person person2;
    private PersonVO personVO1;
    private PersonVO personVO2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        person1 = new Person();
        person1.setId(1L);
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setAddress("123 Main St");
        person1.setGender("Male");

        person2 = new Person();
        person2.setId(2L);
        person2.setFirstName("Jane");
        person2.setLastName("Doe");
        person2.setAddress("456 Main St");
        person2.setGender("Female");

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
    void testFindAll() {
        List<Person> personList = Arrays.asList(person1, person2);
        when(repository.findAll()).thenReturn(personList);

        List<PersonVO> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(personVO1.getId(), result.get(0).getId());
        assertEquals(personVO1.getFirstName(), result.get(0).getFirstName());
        assertEquals(personVO1.getLastName(), result.get(0).getLastName());
        assertEquals(personVO1.getAddress(), result.get(0).getAddress());
        assertEquals(personVO1.getGender(), result.get(0).getGender());
        assertEquals(personVO2.getId(), result.get(1).getId());
        assertEquals(personVO2.getFirstName(), result.get(1).getFirstName());
        assertEquals(personVO2.getLastName(), result.get(1).getLastName());
        assertEquals(personVO2.getAddress(), result.get(1).getAddress());
        assertEquals(personVO2.getGender(), result.get(1).getGender());
    }

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(person1));

        PersonVO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(personVO1.getId(), result.getId());
        assertEquals(personVO1.getFirstName(), result.getFirstName());
        assertEquals(personVO1.getLastName(), result.getLastName());
        assertEquals(personVO1.getAddress(), result.getAddress());
        assertEquals(personVO1.getGender(), result.getGender());
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotExistException.class, () -> {
            service.findById(1L);
        });

        String expectedMessage = "No records found for this ID!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCreate() {
        when(repository.save(any(Person.class))).thenReturn(person1);

        PersonVO result = service.create(personVO1);

        assertNotNull(result);
        assertEquals(personVO1.getId(), result.getId());
        assertEquals(personVO1.getFirstName(), result.getFirstName());
        assertEquals(personVO1.getLastName(), result.getLastName());
        assertEquals(personVO1.getAddress(), result.getAddress());
        assertEquals(personVO1.getGender(), result.getGender());
    }

    @Test
    void testUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(person1));
        when(repository.save(any(Person.class))).thenReturn(person1);

        PersonVO result = service.update(personVO1);

        assertNotNull(result);
        assertEquals(personVO1.getId(), result.getId());
        assertEquals(personVO1.getFirstName(), result.getFirstName());
        assertEquals(personVO1.getLastName(), result.getLastName());
        assertEquals(personVO1.getAddress(), result.getAddress());
        assertEquals(personVO1.getGender(), result.getGender());
    }

    @Test
    void testUpdate_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotExistException.class, () -> {
            service.update(personVO1);
        });

        String expectedMessage = "No records found for this ID!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(person1));
        doNothing().when(repository).delete(person1);

        assertDoesNotThrow(() -> {
            service.delete(1L);
        });

        verify(repository, times(1)).delete(person1);
    }

    @Test
    void testDelete_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotExistException.class, () -> {
            service.delete(1L);
        });

        String expectedMessage = "No records found for this ID!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
