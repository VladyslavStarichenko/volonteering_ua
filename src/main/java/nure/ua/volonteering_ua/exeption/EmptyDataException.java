package nure.ua.volonteering_ua.exeption;



public class EmptyDataException extends RuntimeException{
    private final String message;

    public EmptyDataException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
