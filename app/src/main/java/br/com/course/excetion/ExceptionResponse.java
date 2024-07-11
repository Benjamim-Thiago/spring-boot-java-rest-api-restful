package br.com.course.excetion;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class ExceptionResponse implements Serializable {

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private static final long serialVersionUID = 1L;

	private Date timestamp;
	private String message;
	private String details;
}
