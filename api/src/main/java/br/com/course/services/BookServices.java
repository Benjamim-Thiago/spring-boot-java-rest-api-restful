package br.com.course.services;

import java.util.List;
import java.util.logging.Logger;

import br.com.course.Validation.EntityNotExistException;
import br.com.course.Validation.RequiredObjectIsNullException;
import br.com.course.controllers.BookController;
import br.com.course.data.vo.v1.BookVO;
import br.com.course.mapper.Mapper;
import br.com.course.model.Book;
import br.com.course.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class BookServices {
	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	BookRepository repository;

	@Autowired
	PagedResourcesAssembler<BookVO> assembler;

	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {

		logger.info("Finding all book!");


		List<Sort.Order> orderList = pageable.getSort().toList();
		Sort.Order order = orderList.get(0);

		var booksPage = repository.findAll(pageable);

		var bookVosPage = booksPage.map(p -> Mapper.parseObject(p, BookVO.class));
		bookVosPage.map(
				p -> p.add(
						linkTo(methodOn(BookController.class)
								.findById(p.getKey())).withSelfRel()));

		Link link = linkTo(
				methodOn(BookController.class)
						.findAll(pageable.getPageNumber(),
								pageable.getPageSize(),
								order.getDirection().toString(),
								order.getProperty())).withSelfRel();

		return assembler.toModel(bookVosPage, link);
	}

	public BookVO findById(Long id) {
		
		logger.info("Finding one book!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));
		var vo = Mapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO book) {

		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		var entity = Mapper.parseObject(book, Book.class);
		var vo =  Mapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) {

		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");
		
		var entity = repository.findById(book.getKey())
			.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));

		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo =  Mapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotExistException("No records found for this ID!"));
		repository.delete(entity);
	}
}
