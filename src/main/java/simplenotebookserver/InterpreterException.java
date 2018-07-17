package simplenotebookserver;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InterpreterException extends Exception {

	public InterpreterException(String message) {
		// TODO Auto-generated constructor stub
		super(message);
	}

}
