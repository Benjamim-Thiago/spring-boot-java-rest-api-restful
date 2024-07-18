package br.com.course.unittestes.mockito.services;

import br.com.course.Validation.RequiredObjectIsNullException;
import br.com.course.data.vo.v1.BookVO;
import br.com.course.model.Book;
import br.com.course.repositories.BookRepository;
import br.com.course.services.BookServices;
import br.com.course.unittestes.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {

	MockBook input;
	
	@InjectMocks
	private BookServices service;
	
	@Mock
	BookRepository repository;
	
	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() {
		Book entity = input.mockEntity(1); 
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		
		var result = service.findById(1L);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test 1", result.getAuthor());
		assertEquals(10.50 + entity.getId(), result.getPrice());
		assertEquals("Test book 1", result.getTitle());
		assertEquals(LocalDate.of(2024, 7, 18), result.getLaunchDate());
	}
	
	@Test
	void testCreate() {
		Book entity = input.mockEntity(1); 
		entity.setId(1L);
		
		Book persisted = entity;
		persisted.setId(1L);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.create(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test 1", result.getAuthor());
		assertEquals(10.50 + vo.getKey(), result.getPrice());
		assertEquals("Test book 1", result.getTitle());
		assertEquals(LocalDate.of(2024, 7, 18), result.getLaunchDate());
	}
	
	@Test
	void testCreateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}


	@Test
	void testUpdate() {
		Book entity = input.mockEntity(1); 
		
		Book persisted = entity;
		persisted.setId(1L);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		

		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.update(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());

		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test 1", result.getAuthor());
		assertEquals(10.50 + vo.getKey(), result.getPrice());
		assertEquals("Test book 1", result.getTitle());
		assertEquals(LocalDate.of(2024, 7, 18), result.getLaunchDate());
	}
	

	
	@Test
	void testUpdateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testDelete() {
		Book entity = input.mockEntity(1); 
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		
		service.delete(1L);
	}
	
	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList(); 
		
		when(repository.findAll()).thenReturn(list);
		
		var people = service.findAll();
		
		assertNotNull(people);
		assertEquals(14, people.size());
		
		var bookOne = people.get(1);
		
		assertNotNull(bookOne);
		assertNotNull(bookOne.getKey());
		assertNotNull(bookOne.getLinks());

		assertTrue(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test 1", bookOne.getAuthor());
		assertEquals(10.50 + bookOne.getKey(), bookOne.getPrice());
		assertEquals("Test book 1", bookOne.getTitle());
		assertEquals(LocalDate.of(2024, 7, 18), bookOne.getLaunchDate());
		
		var bookFour = people.get(4);
		
		assertNotNull(bookFour);
		assertNotNull(bookFour.getKey());
		assertNotNull(bookFour.getLinks());
		
		assertTrue(bookFour.toString().contains("links: [</api/book/v1/4>;rel=\"self\"]"));
		assertEquals("Author Test 4", bookFour.getAuthor());
		assertEquals(10.50 + bookFour.getKey(), bookFour.getPrice());
		assertEquals("Test book 4", bookFour.getTitle());
		assertEquals(LocalDate.of(2024, 7, 18), bookFour.getLaunchDate());
		
		var bookSeven = people.get(7);
		
		assertNotNull(bookSeven);
		assertNotNull(bookSeven.getKey());
		assertNotNull(bookSeven.getLinks());
		
		assertTrue(bookSeven.toString().contains("links: [</api/book/v1/7>;rel=\"self\"]"));
		assertEquals("Author Test 7", bookSeven.getAuthor());
		assertEquals(10.50 + bookSeven.getKey(), bookSeven.getPrice());
		assertEquals("Test book 7", bookSeven.getTitle());
		assertEquals(LocalDate.of(2024, 7, 18), bookSeven.getLaunchDate());

	}

}
