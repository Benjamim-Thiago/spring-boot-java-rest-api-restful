package br.com.course.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import br.com.course.data.vo.v1.PersonVO;
import br.com.course.mapper.Mapper;
import br.com.course.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapperTest {

    private Person person;
    private PersonVO personVO;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("123 Main St");
        person.setGender("Male");

        personVO = new PersonVO();
        personVO.setId(1L);
        personVO.setFirstName("John");
        personVO.setLastName("Doe");
        personVO.setAddress("123 Main St");
        personVO.setGender("Male");
    }

    @Test
    void testParseObject() {
        PersonVO vo = Mapper.parseObject(person, PersonVO.class);

        assertNotNull(vo);
        assertEquals(person.getId(), vo.getId());
        assertEquals(person.getFirstName(), vo.getFirstName());
        assertEquals(person.getLastName(), vo.getLastName());
        assertEquals(person.getAddress(), vo.getAddress());
        assertEquals(person.getGender(), vo.getGender());
    }

    @Test
    void testParseListObjects() {
        List<Person> personList = Arrays.asList(person);
        List<PersonVO> voList = Mapper.parseListObjects(personList, PersonVO.class);

        assertNotNull(voList);
        assertEquals(1, voList.size());

        PersonVO vo = voList.get(0);
        assertEquals(person.getId(), vo.getId());
        assertEquals(person.getFirstName(), vo.getFirstName());
        assertEquals(person.getLastName(), vo.getLastName());
        assertEquals(person.getAddress(), vo.getAddress());
        assertEquals(person.getGender(), vo.getGender());
    }

    @Test
    void testParseListObjects_EmptyList() {
        List<Person> emptyList = Arrays.asList();
        List<PersonVO> voList = Mapper.parseListObjects(emptyList, PersonVO.class);

        assertNotNull(voList);
        assertTrue(voList.isEmpty());
    }
}
