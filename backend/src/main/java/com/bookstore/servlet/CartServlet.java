package com.bookstore.servlet;
import com.bookstore.dao.*; import com.bookstore.model.*; import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import java.io.*; import java.util.*;
@WebServlet("/api/cart")
public class CartServlet extends HttpServlet {
    private final CartDAO dao = new CartDAO(); private final Gson gson = new Gson();
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try { resp.getWriter().print(gson.toJson(dao.getCartByUserId(Integer.parseInt(req.getParameter("userId"))))); }
        catch (Exception e) { resp.setStatus(500); }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8"); JsonObject b = gson.fromJson(req.getReader(), JsonObject.class);
        try { resp.getWriter().print(gson.toJson(dao.addToCart(b.get("userId").getAsInt(), b.get("bookId").getAsInt(), b.has("quantity")?b.get("quantity").getAsInt():1))); }
        catch (Exception e) { resp.setStatus(500); }
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8"); JsonObject b = gson.fromJson(req.getReader(), JsonObject.class);
        try { resp.getWriter().print(gson.toJson(dao.updateQuantity(b.get("cartId").getAsInt(), b.get("quantity").getAsInt()))); }
        catch (Exception e) { resp.setStatus(500); }
    }
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try { dao.removeFromCart(Integer.parseInt(req.getParameter("cartId"))); }
        catch (Exception e) { resp.setStatus(500); }
    }
}
