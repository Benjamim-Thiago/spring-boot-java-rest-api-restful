package br.com.course.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import br.com.course.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.course.configs.TestConfigs;
import br.com.course.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.course.integrationtests.vo.AccountCredentialsVO;
import br.com.course.integrationtests.vo.PersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerCorsJsonTest extends AbstractIntegrationTest {
	
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;

	private static PersonVO person;

	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		person = new PersonVO();
	}

	private  RequestSpecification  makeSpecification() {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var accessToken = given()
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(user)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenVO.class)
				.getAccessToken();

		return new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	private PersonVO createPerson() throws JsonProcessingException {
		return factoryCreatePerson("");
	}


	private List<PersonVO> createPeople(Integer amount) throws JsonProcessingException {
		List<PersonVO> people = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			people.add(factoryCreatePerson(" " + i));
		}

		return people;
	}

	private  PersonVO factoryCreatePerson(String complement) throws JsonProcessingException {
		mockPerson(complement);

		if(specification == null) {
			specification = this.makeSpecification();
		}

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				//.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.body(person)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();

        return objectMapper.readValue(content, PersonVO.class);
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		specification = this.makeSpecification();
	}

	@Test
	@Order(1)
	public void testCreate() {
		try {
			PersonVO persistedPerson = createPerson();

			assertNotNull(persistedPerson);

			assertNotNull(persistedPerson.getId());
			assertNotNull(persistedPerson.getFirstName());
			assertNotNull(persistedPerson.getLastName());
			assertNotNull(persistedPerson.getAddress());
			assertNotNull(persistedPerson.getGender());
			assertTrue(persistedPerson.getEnabled());

			assertTrue(persistedPerson.getId() > 0);
			assertTrue(persistedPerson.getEnabled());

			assertEquals("Richard", persistedPerson.getFirstName());
			assertEquals("Stallman", persistedPerson.getLastName());
			assertEquals("New York City, New York, US", persistedPerson.getAddress());
			assertEquals("Male", persistedPerson.getGender());
			assertEquals(true, persistedPerson.getEnabled());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPerson("");

		if(specification == null) {
			specification = this.makeSpecification();
		}

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
					.body(person)
				.when()
					.post()
				.then()
					.statusCode(403)
						.extract()
							.body()
								.asString();

		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		// Create before person
		person = createPerson();

		if(specification == null) {
			specification = this.makeSpecification();
		}

		//Test find after

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					//.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();

		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		// Create before person
		person = createPerson();

		if(specification == null) {
			specification = this.makeSpecification();
		}

		//Test find after

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(403)
						.extract()
						.body()
							.asString();


		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(5)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		person = createPerson();

		if(specification == null) {
			specification = this.makeSpecification();
		}
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();

		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Stallman", persistedPerson.getLastName());
		assertEquals("New York City, New York, US", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		// Create a person to delete
		person = createPerson();

		if (specification == null) {
			specification = this.makeSpecification();
		}

		// Delete the created person
		assertNotNull(
				given().spec(specification)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.when()
					.delete("{id}")
					.then()
					.statusCode(204)
		); // No Content

		// Try to find the deleted person
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(404) // Not Found
				.extract()
				.body()
				.asString();

		JsonNode jsonNode = objectMapper.readTree(content);
		String detail = jsonNode.get("detail").asText();

		assertNotNull(content);
		assertEquals("No records found for this ID!", detail);
	}

	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		// Create multiple people
		List<PersonVO> people = createPeople(5);

		if (specification == null) {
			specification = this.makeSpecification();
		}

		// Test find all
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();

		List<PersonVO> foundPeople = objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, PersonVO.class));

		assertNotNull(foundPeople);
		assertTrue(foundPeople.size() >= 1);

		for (PersonVO p : people) {
			assertTrue(foundPeople.stream().anyMatch(fp -> fp.getId().equals(p.getId())));
		}
	}

	private void mockPerson(String complement) {
		person.setFirstName("Richard" + complement);
		person.setLastName("Stallman" + complement);
		person.setAddress("New York City, New York, US");
		person.setGender("Male");
		person.setEnabled(true);
	}

}
