package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	public ValidationException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> errors = new HashMap<>();

	public Map<String, String> getErrors() {
		return errors;
	}
	
	public void addError(String fieldName, String error) {
		errors.put(fieldName, error);
	}
	


}
