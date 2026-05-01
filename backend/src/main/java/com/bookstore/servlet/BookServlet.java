package com.bookstore.servlet;
import com.bookstore.dao.*; import com.bookstore.model.*; import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import java.io.*; import java.util.*;
@WebServlet("/api/books")
public class BookServlet extends HttpServlet {
    private final BookDAO dao = new BookDAO(); private final Gson gson = new Gson();
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8"); String s = req.getParameter("search");
        try { List<Book> list = (s != null && !s.isEmpty()) ? dao.searchBooks(s) : dao.getAllBooks(); resp.getWriter().print(gson.toJson(list)); }
        catch (Exception e) { 
            resp.setStatus(500); 
            e.printStackTrace(resp.getWriter()); 
        }
    }
}
