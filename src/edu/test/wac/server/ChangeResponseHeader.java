package edu.test.wac.server;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ChangeResponseHeader implements javax.servlet.Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

		HttpServletResponse myResponse = (HttpServletResponse) arg1;
        myResponse.addHeader("Access-Control-Allow-Origin", "*");
        myResponse.addHeader("Access-Control-Allow-Headers", "content-type,x-gwt-module-base,x-gwt-permutation"); 
        
        arg2.doFilter(arg0, myResponse);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
}