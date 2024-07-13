package br.com.course.excetion.handler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.course.excetion.Problem;
import br.com.course.excetion.ProblemType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;

import br.com.course.Validation.*;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String GENERIC_MESSAGE_USER_FINAL = "Ocorreu um erro interno inesperado no sistema. "
            + "Tente novamente e se o problema persistir, entre em contato " + "com o administrador do sistema.";

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(status).headers(headers).build();
    }

    private ResponseEntity<Object> handleValidationInternal(Exception ex, HttpHeaders headers, HttpStatusCode status,
                                                            WebRequest request, BindingResult bindingResult) {
        ProblemType problemType = ProblemType.INVALID_DATA;
        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

        List<Problem.Object> problemObjects = bindingResult.getAllErrors().stream().map(objectError -> {
            String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());

            String name = objectError.getObjectName();

            if (objectError instanceof FieldError) {
                name = ((FieldError) objectError).getField();
            }

            Problem.Object problemObject = new Problem.Object();
            problemObject.setName(name);
            problemObject.setUserMessage(message);
            return problemObject;
        }).collect(Collectors.toList());

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(detail);
        problem.setObjects(problemObjects);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler({ ValidationException.class })
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        return handleValidationInternal(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, request, ex.getBindingResult());
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleValidationInternal(ex, headers, status, request, ex.getBindingResult());

    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatusCode status, WebRequest request) {

        ProblemType problemType = ProblemType.RESOURCE_NOT_FOUND;
        String detail = String.format("O recurso %s, que você tentou acessar, é inexistente.", ex.getRequestURL());

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(GENERIC_MESSAGE_USER_FINAL);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    // Error formated JSON
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
        } else if (rootCause instanceof PropertyBindingException) {
            return handlePropertyBindingException((PropertyBindingException) rootCause, headers, status, request);
        }

        ProblemType problemType = ProblemType.INCOMPATIBLE_BODY;
        String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(GENERIC_MESSAGE_USER_FINAL);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, HttpHeaders headers,
                                                                HttpStatusCode status, WebRequest request) {

        String path = ex.getPath().stream().map(ref -> ref.getFieldName()).collect(Collectors.joining("."));

        ProblemType problemType = ProblemType.INCOMPATIBLE_BODY;
        String detail = String.format(
                "A propriedade '%s' recebeu o valor '%s', "
                        + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
                path, ex.getValue(), ex.getTargetType().getSimpleName());

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(GENERIC_MESSAGE_USER_FINAL);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ResponseEntity<Object> handlePropertyBindingException(PropertyBindingException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {

        // I created the joinPath method to reuse in all methods that need it
        // concatenate property names (separating by ".")
        String path = joinPath(ex.getPath());

        ProblemType problemType = ProblemType.INCOMPATIBLE_BODY;
        String detail = String.format(
                "A propriedade '%s' não existe. Corrija ou remova essa propriedade e tente novamente.", path);

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(GENERIC_MESSAGE_USER_FINAL);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(EntityNotExistException.class)
    // Entity not found (Not Found)
    public ResponseEntity<?> handlerEntityNotExistException(EntityNotExistException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.RESOURCE_NOT_FOUND;
        String detail = ex.getMessage();

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(GENERIC_MESSAGE_USER_FINAL);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(BusinessException.class)
    // Business exception
    public ResponseEntity<?> handlerBusinessException(BusinessException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.BUSINESS_ERROR;
        String detail = ex.getMessage();

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(GENERIC_MESSAGE_USER_FINAL);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EntityInUseException.class)
    // Entity in use
    public ResponseEntity<?> handlerEntityInUseException(EntityInUseException ex, WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        ProblemType problemType = ProblemType.ENTITY_IN_USE;
        String detail = ex.getMessage();

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(detail);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest request) {

        if (ex instanceof MethodArgumentTypeMismatchException) {
            return handleMethodArgumentTypeMismatch((MethodArgumentTypeMismatchException) ex, headers, status, request);
        }

        return super.handleTypeMismatch(ex, headers, status, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                    HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemType problemType = ProblemType.INVALID_PARAM;

        String detail = String.format(
                "O parâmetro de URL '%s' recebeu o valor '%s', "
                        + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(GENERIC_MESSAGE_USER_FINAL);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode status, WebRequest request) {
        if (body == null) {
            body = new Problem();
            ((Problem) body).setTitle(HttpStatus.valueOf(status.value()).getReasonPhrase());
            ((Problem) body).setStatus(status.value());
            ((Problem) body).setTimestamp(OffsetDateTime.now());
        } else if (body instanceof String) {
            body = new Problem();
            ((Problem) body).setTitle((String) body);
            ((Problem) body).setStatus(status.value());
            ((Problem) body).setTimestamp(OffsetDateTime.now());
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private Problem createProblemBuilder(HttpStatus status, ProblemType problemType, String detail) {
        Problem problem = new Problem();
        problem.setStatus(status.value());
        problem.setTimestamp(OffsetDateTime.now());
        problem.setType(problemType.getUri());
        problem.setTitle(problemType.getTitle());
        problem.setDetail(detail);
        problem.setUserMessage(detail);

        return problem;
    }

    private String joinPath(List<Reference> references) {
        return references.stream().map(ref -> ref.getFieldName()).collect(Collectors.joining("."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType problemType = ProblemType.SYSTEM_ERROR;
        String detail = GENERIC_MESSAGE_USER_FINAL;

        // Importante colocar o printStackTrace (pelo menos por enquanto, que não
        // estamos
        // fazendo logging) para mostrar a stacktrace no console
        // Se não fizer isso, você não vai ver a stacktrace de exceptions que seriam
        // importantes
        // para você durante, especialmente na fase de desenvolvimento
        ex.printStackTrace();

        Problem problem = createProblemBuilder(status, problemType, detail);

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

//TODO  Fazer no modulo de springsecurity do curso
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
//
//        HttpStatus status = HttpStatus.FORBIDDEN;
//        ProblemType problemType = ProblemType.ACCESS_DENIED;
//        String detail = ex.getMessage();
//
//        Problem problem = createProblemBuilder(status, problemType, detail)
//                .userMessage(detail)
//                .userMessage("Você não possui permissão para executar essa operação.")
//                .build();
//
//        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
//    }

}