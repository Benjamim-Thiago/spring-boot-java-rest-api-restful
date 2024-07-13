package br.com.course.services;

import java.util.List;
import java.util.logging.Logger;

import br.com.course.Validation.EntityInUseException;
import br.com.course.Validation.EntityNotExistException;
import br.com.course.data.vo.v1.model.PersonVO;
import br.com.course.mapper.Mapper;
import br.com.course.model.Person;
import br.com.course.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

		return Mapper.parseListObjects(repository.findAll(), PersonVO.class);
	}

	public PersonVO findById(Long id) {
		
		logger.info("Finding one person!");
		var entity = repository.findById(id)
			.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));
		return Mapper.parseObject(entity, PersonVO.class);
	}

	@Transactional
	public PersonVO create(PersonVO person) {
        logger.info("Creating one person!");
        var entity = Mapper.parseObject(person, Person.class);
        var vo =  Mapper.parseObject(repository.save(entity), PersonVO.class);
        return vo;
    }
	
	public PersonVO update(PersonVO person) {
		
		logger.info("Updating one person!");
		
		var entity = repository.findById(person.getId())
			.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo =  Mapper.parseObject(repository.save(entity), PersonVO.class);
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
