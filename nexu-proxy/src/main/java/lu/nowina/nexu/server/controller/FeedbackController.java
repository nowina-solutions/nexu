package lu.nowina.nexu.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.server.business.FeedbackManager;

@Controller
public class FeedbackController {

	@Autowired
	private FeedbackManager feedbackService;
	
	@RequestMapping(value = "/feedback", method=RequestMethod.POST, consumes = "application/xml")
	public ResponseEntity<String> reportError(@RequestBody Feedback feedback) throws Exception {
		feedbackService.reportError(feedback);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
