package br.com.course.services;

import br.com.course.model.Person;
import br.com.course.repositories.PersonRepository;
import br.com.course.excetion.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonServicesTest {

    @Mock
    private PersonRepository repository;

    @InjectMocks
    private PersonServices personServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Person> expectedPersons = new ArrayList<>();

        Person person01 = new Person();
        person01.setId(1L);
        person01.setFirstName("John");
        person01.setLastName("Doe");
        person01.setAddress("123 Street");
        person01.setGender("Male");

        Person person02 = new Person();
        person02.setId(2L);
        person02.setFirstName("Jane");
        person02.setLastName("Doe");
        person02.setAddress("456 Avenue");
        person02.setGender("Female");

        expectedPersons.add(person01);
        expectedPersons.add(person02);

        when(repository.findAll()).thenReturn(expectedPersons);

        List<Person> actualPersons = personServices.findAll();
        assertEquals(expectedPersons.size(), actualPersons.size());
        assertEquals(expectedPersons.get(0), actualPersons.get(0));
        assertEquals(expectedPersons.get(1), actualPersons.get(1));
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById_Exists() {
        Person expectedPerson = new Person();
        expectedPerson.setId(1L);
        expectedPerson.setFirstName("John");
        expectedPerson.setLastName("Doe");
        expectedPerson.setAddress("123 Street");
        expectedPerson.setGender("Male");

        when(repository.findById(expectedPerson.getId())).thenReturn(Optional.of(expectedPerson));

        Person actualPerson = personServices.findById(expectedPerson.getId());
        assertEquals(expectedPerson, actualPerson);
        verify(repository, times(1)).findById(expectedPerson.getId());
    }

    @Test
    void testFindById_NotFound() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personServices.findById(id));
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testCreate() {
        Person person = new Person();
        person.setId(null);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Street");
        person.setGender("Male");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFirstName("John");
        savedPerson.setLastName("Doe");
        savedPerson.setAddress("123 Street");
        savedPerson.setGender("Male");

        when(repository.save(any(Person.class))).thenReturn(savedPerson);

        Person createdPerson = personServices.create(person);
        assertEquals(savedPerson, createdPerson);
        verify(repository, times(1)).save(person);
    }

    @Test
    void testUpdate() {
        Person personToUpdate = new Person();
        personToUpdate.setId(1L);
        personToUpdate.setFirstName("John");
        personToUpdate.setLastName("Doe");
        personToUpdate.setAddress("123 Street");
        personToUpdate.setGender("Male");

        Person updatedPerson = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setFirstName("Updated John");
        updatedPerson.setLastName("Updated Doe");
        updatedPerson.setAddress("456 Avenue");
        updatedPerson.setGender("Female");

        when(repository.findById(personToUpdate.getId())).thenReturn(Optional.of(personToUpdate));
        when(repository.save(any(Person.class))).thenReturn(updatedPerson);

        Person returnedPerson = personServices.update(updatedPerson);
        assertEquals(updatedPerson, returnedPerson);
        verify(repository, times(1)).findById(personToUpdate.getId());
        verify(repository, times(1)).save(updatedPerson);
    }

    @Test
    void testUpdate_NotFound() {
        Person updatedPerson = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setFirstName("Jane");
        updatedPerson.setLastName("Doe");
        updatedPerson.setAddress("456 Avenue");
        updatedPerson.setGender("Female");

        when(repository.findById(updatedPerson.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personServices.update(updatedPerson));
        verify(repository, times(1)).findById(updatedPerson.getId());
        verify(repository, never()).save(updatedPerson);
    }

    @Test
    void testDelete() {
        Person personToDelete = new Person();
        personToDelete.setId(1L);
        personToDelete.setFirstName("Jane");
        personToDelete.setLastName("Doe");
        personToDelete.setAddress("456 Avenue");
        personToDelete.setGender("Female");

        when(repository.findById(personToDelete.getId())).thenReturn(Optional.of(personToDelete));

        personServices.delete(personToDelete.getId());
        verify(repository, times(1)).findById(personToDelete.getId());
        verify(repository, times(1)).delete(personToDelete);
    }

    @Test
    void testDelete_NotFound() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personServices.delete(id));
        verify(repository, times(1)).findById(id);
        verify(repository, never()).delete(any(Person.class));
    }
}
