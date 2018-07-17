package simplenotebookserver;

import java.io.StringWriter;
import java.util.HashMap;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CodeController {

	ScriptContext context = new SimpleScriptContext();
	HashMap<Integer, ScriptContext> contextHashMap = new HashMap<Integer, ScriptContext>();

	
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public ResponseEntity<Result> execute(@RequestBody Code code, @RequestParam(value="sessionId", required=false) Integer sessionId) throws Exception {
    	
    	//get user input
    	String interpreterPart = new String(); 
    	String interpreterName = new String(); 
    	String command = new String();
    	
    	try {
	    	interpreterPart = code.getInterpreterPart(); // %python
	    	interpreterName = code.getInterpreterName(); // python
	    	command = code.getCommand(); // print 1+1
	    	
	    	System.out.println("part: "+interpreterPart);
	    	System.out.println("name: "+interpreterName);
	    	System.out.println("command: "+command);
    	}
    	catch (Exception e) {
    		throw new FormatException("Your code does not respect the right format: %<interpreter-name><whitespace><code>");
    	}

		if (!interpreterName.equals("python")) {	
			throw new InterpreterException("Unknown interpreter '"+interpreterName+"'. Interpreter must be 'python'");
		}
		else {
			
			//get Python engine
			ScriptEngineManager manager = new ScriptEngineManager();
    		ScriptEngine engine = manager.getEngineByName("python");
    		
    		Result result = new Result("");
    		
    		if (sessionId != null) {
    			
    			ScriptContext newContext = new SimpleScriptContext();
				StringWriter newWriter = new StringWriter();
				
    			if(contextHashMap.get(sessionId) != null) {
    				
    				newContext = contextHashMap.get(sessionId);
    				newContext.setWriter(newWriter);

    				//evaluate python code
    				engine.eval(command, newContext);

    				//update context in hashmap for this sessionId
    				contextHashMap.put(sessionId, newContext);
			
    			}
    			else {
    				
    				newContext.setWriter(newWriter);
    				
    				//evaluate python code
    				engine.eval(command, newContext);

    				//save context in hashmap for this sessionId
    				contextHashMap.put(sessionId, newContext);
    				
    			}
    			
    			result.setResult(newWriter.toString().replace("\n", ""));
    			
    		}
    		else {
    			
    			StringWriter writer = new StringWriter();
    			context.setWriter(writer);

    			engine.eval(command, context);
				
				result.setResult(writer.toString().replace("\n", ""));
    			
    		}
    		
    		return new ResponseEntity<Result>(result, HttpStatus.OK);
    		
		}
    	
    }
    

}
