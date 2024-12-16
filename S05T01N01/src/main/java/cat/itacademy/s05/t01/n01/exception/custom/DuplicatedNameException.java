package cat.itacademy.s05.t01.n01.exception.custom;

public class DuplicatedNameException extends RuntimeException{
    public DuplicatedNameException(String message){
        super(message);
    }
}
