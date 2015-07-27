package demo.baidu.lbs.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class AuthFilter implements Filter {
	private static final Logger log = Logger.getLogger(AuthFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		debugRequest(request);
		// TODO all not rest request pass by
		// if (!"/rest".equals(request.getServletPath())) {
		// chain.doFilter(req, res);
		// return;
		// }

		chain.doFilter(req, res);
	}

	private void debugRequest(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			String strBackUrl = request.getRequestURL().toString();
			String queryParams = request.getQueryString();
			if (queryParams != null) {
				strBackUrl = request.getRequestURL() + "?" + request.getQueryString();
			}
			log.error("REQUEST_URL=" + strBackUrl);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
