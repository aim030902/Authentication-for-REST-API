package uz.aim.zerikdim5.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {
    private String message;
    private T object;
    private boolean success;
    private ApiErrorResponse error;


    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public ApiResponse(String message, T object, boolean success) {
        this.message = message;
        this.object = object;
        this.success = success;
    }


    public ApiResponse(ApiErrorResponse error) {
        this.error = error;
        this.success = false;
    }
}
