package com.bookstore.servlet;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Handles GET /api/books
 * <p>
 * Query parameter: {@code search} – optional keyword to filter by title or author.
 */
@WebServlet("/api/books")
public class BookServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final Gson    gson    = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String search = req.getParameter("search");
            List<Book> books;
            if (search != null && !search.trim().isEmpty()) {
                books = bookDAO.searchBooks(search.trim());
            } else {
                books = bookDAO.getAllBooks();
            }
            out.print(gson.toJson(books));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Database error: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }
}
