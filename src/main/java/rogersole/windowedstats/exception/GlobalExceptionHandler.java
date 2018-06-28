package rogersole.windowedstats.exception;

import lombok.val;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import rogersole.windowedstats.model.Error;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let the framework handle it
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        // Otherwise setup and send the user to a default error-view.
        val mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Error methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        val result = ex.getBindingResult();
        return processFieldErrors(result.getFieldErrors());
    }

    private Error processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
        val error = new Error(BAD_REQUEST.value(), "validation error");
        for (org.springframework.validation.FieldError fieldError : fieldErrors) {
            error.addErrorField(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error;
    }
}
