package simplenotebookserver;

public class Code {
	
	private String code;

	public Code() {
	        super();
	}
	 
    public Code(String code) {
        this.code = code;
    }
    
	public String getCode() {
        return code;
    }
	
	public String getInterpreterPart() {
		return code.split("\\s+")[0];
	}
	
	public String getInterpreterName() {
		return getInterpreterPart().split("%")[1];
	}
	
	public String getCommand() {
		return code.split("\\s+", 2)[1];
	}
}
