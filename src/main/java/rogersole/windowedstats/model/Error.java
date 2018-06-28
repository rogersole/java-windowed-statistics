package rogersole.windowedstats.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Error {

    private final int status;
    private final String message;
    private List<FieldError> fieldErrors = new ArrayList<>();

    public Error(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public void addErrorField(String objectName, String path, String message) {
        fieldErrors.add(new FieldError(objectName, path, message));
    }
}