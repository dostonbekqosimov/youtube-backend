package dasturlash.uz.exceptions;


import lombok.Getter;

@Getter
public class DataExistsException extends RuntimeException {
    private Long id;

    public DataExistsException(String message) {
        super(message);
    }

    public DataExistsException(String message, Long id) {
        super(message + id);
        this.id = id;
    }
}
