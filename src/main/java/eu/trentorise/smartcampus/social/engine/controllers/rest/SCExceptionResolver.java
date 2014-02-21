package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

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
			}
		}

		return super.doResolveException(request, response, handler, ex);
	}

	private ModelAndView resolveIllegalArgumentException(HttpServletResponse response,
			Exception exception) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST,
				exception.getMessage());
		return new ModelAndView();
	}

}
