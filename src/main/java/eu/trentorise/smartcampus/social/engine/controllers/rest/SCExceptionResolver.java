package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import eu.trentorise.smartcampus.social.engine.beans.Result;

public class SCExceptionResolver extends DefaultHandlerExceptionResolver {
	private static final Logger logger = Logger
			.getLogger(SCExceptionResolver.class);

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {

		if (ex instanceof IllegalArgumentException) {
			try {
				return resolveIllegalArgumentException(response, ex);
			} catch (IOException e) {
				logger.error("Exception resolving IllegalArgumentException");
			} catch (Exception e){
				logger.error(String.format("Exception '%s' founded.", e.getMessage()));
			}
		}

		return super.doResolveException(request, response, handler, ex);
	}

	private ModelAndView resolveIllegalArgumentException(HttpServletResponse response,
			Exception exception) throws IOException, Exception {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().write(Result.resultToJsonString(new Result(exception, HttpServletResponse.SC_BAD_REQUEST)));
		
		/*response.sendError(HttpServletResponse.SC_BAD_REQUEST,
				exception.getMessage());*/
		return new ModelAndView();
	}
	
	
	
	@Override
	protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		try {
			response.getWriter().write(Result.resultToJsonString(new Result(ex, HttpServletResponse.SC_BAD_REQUEST)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView();
	}

}
