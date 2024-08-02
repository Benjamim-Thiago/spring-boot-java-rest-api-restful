package br.com.course.services;

import java.util.List;
import java.util.logging.Logger;

import br.com.course.Validation.EntityInUseException;
import br.com.course.Validation.EntityNotExistException;
import br.com.course.Validation.RequiredObjectIsNullException;
import br.com.course.controllers.PersonController;
import br.com.course.data.vo.v1.PersonVO;
import br.com.course.mapper.Mapper;
import br.com.course.model.Person;
import br.com.course.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	PersonRepository repository;

	public List<PersonVO> findAll() {

		logger.info("Finding all people!");

		var persons = Mapper.parseListObjects(repository.findAll(), PersonVO.class);
		persons
				.stream()
				.forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		return persons;
	}

	public PersonVO findById(Long id) {
		
		logger.info("Finding one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));
		var vo = Mapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}

	@Transactional
	public PersonVO create(PersonVO person) {
		if (person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");

		var entity = Mapper.parseObject(person, Person.class);
		var vo =  Mapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
    }

	@Transactional
	public PersonVO update(PersonVO person) {
		if (person == null) throw new RequiredObjectIsNullException();

		logger.info("Updating one person!");

		var entity = repository.findById(person.getKey())
				.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());

		var vo =  Mapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	@Transactional
	public PersonVO disablePerson(Long id) {

		logger.info("Disabling one person!");

		repository.disablePerson(id);

		var entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));
		var vo = Mapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		try {
			logger.info("Deleting one person!");

			var entity = repository.findById(id)
					.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));
			repository.delete(entity);
		} catch (EmptyResultDataAccessException e) {
			throw new EntityNotExistException("No records found for this ID!");
		} catch (DataIntegrityViolationException e) {
			throw new EntityInUseException("Entity cannot be removed as there is information linked to the entity");
		}
	}
}
