package simplenotebookserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CodeControllerIT {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testSimpleStatementExecution() throws Exception {
		
		Code code = new Code("%python print 1+1");

		HttpEntity<Code> entity = new HttpEntity<Code>(code, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, String.class);

		String expected = "{\"result\":\"2\"}";

		JSONAssert.assertEquals(expected, response.getBody(), false);
		
	}
	
	@Test
	public void testStatePreservation() throws Exception {
		
		//first POST
		Code code = new Code("%python a=1");

		HttpEntity<Code> entity = new HttpEntity<Code>(code, headers);

		ResponseEntity<String> response1 = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity, String.class);

		String expected1 = "{\"result\":\"\"}";

		JSONAssert.assertEquals(expected1, response1.getBody(), false);
		
		//Second POST
		Code code2 = new Code("%python print a");

		HttpEntity<Code> entity2 = new HttpEntity<Code>(code, headers);
		
		ResponseEntity<String> response2 = restTemplate.exchange(
				createURLWithPort("/execute"),
				HttpMethod.POST, entity2, String.class);

		String expected2 = "{\"result\":\"1\"}";
		
	}
	
	@Test
	public void testExecutionBySession() throws Exception {
		
		//first POST
		Code code = new Code("%python a=10");

		HttpEntity<Code> entity = new HttpEntity<Code>(code, headers);

		ResponseEntity<String> response1 = restTemplate.exchange(
				createURLWithPort("/execute?sessionId=1"),
				HttpMethod.POST, entity, String.class);

		String expected1 = "{\"result\":\"\"}";

		JSONAssert.assertEquals(expected1, response1.getBody(), false);
		
		//Second POST
		Code code2 = new Code("%python print a");

		HttpEntity<Code> entity2 = new HttpEntity<Code>(code2, headers);
		
		ResponseEntity<String> response2 = restTemplate.exchange(
				createURLWithPort("/execute?sessionId=2"),
				HttpMethod.POST, entity2, String.class);

		String expected2 = "500";
		
		JSONAssert.assertEquals(expected2, response2.getStatusCode().toString(), false);
		
	}
	
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}


}
