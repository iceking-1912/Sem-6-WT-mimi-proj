package com.bookstore.filter;
import jakarta.servlet.*; import jakarta.servlet.annotation.WebFilter; import jakarta.servlet.http.*; import java.io.IOException;
@WebFilter("/*")
public class CORSFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse r = (HttpServletResponse) res;
        r.setHeader("Access-Control-Allow-Origin", "*");
        r.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        r.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
        if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest)req).getMethod())) { r.setStatus(200); return; }
        chain.doFilter(req, res);
    }
}
