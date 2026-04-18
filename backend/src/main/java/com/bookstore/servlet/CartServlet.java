package com.bookstore.servlet;

import com.bookstore.dao.CartDAO;
import com.bookstore.model.CartItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Manages the shopping cart.
 * <p>
 * GET    /api/cart?userId={id}                    – list cart items<br>
 * POST   /api/cart   body: {userId, bookId, quantity} – add / increment item<br>
 * PUT    /api/cart   body: {cartId, quantity}         – update quantity<br>
 * DELETE /api/cart?cartId={id}                    – remove item
 */
@WebServlet("/api/cart")
public class CartServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();
    private final Gson    gson    = new Gson();

    // ---- GET /api/cart?userId={id} ----------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String raw = req.getParameter("userId");
        if (raw == null || raw.isBlank()) {
            error(resp, out, 400, "userId is required");
            return;
        }

        try {
            int userId = Integer.parseInt(raw);
            List<CartItem> items = cartDAO.getCartByUserId(userId);
            out.print(gson.toJson(items));
        } catch (NumberFormatException e) {
            error(resp, out, 400, "userId must be an integer");
        } catch (SQLException e) {
            error(resp, out, 500, "Database error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    // ---- POST /api/cart ---------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            JsonObject body = parseBody(req);

            if (!body.has("userId") || !body.has("bookId")) {
                error(resp, out, 400, "userId and bookId are required");
                return;
            }

            int userId   = body.get("userId").getAsInt();
            int bookId   = body.get("bookId").getAsInt();
            int quantity = body.has("quantity") ? body.get("quantity").getAsInt() : 1;

            if (quantity < 1) {
                error(resp, out, 400, "Quantity must be at least 1");
                return;
            }

            CartItem item = cartDAO.addToCart(userId, bookId, quantity);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(item));
        } catch (JsonSyntaxException e) {
            error(resp, out, 400, "Invalid JSON body");
        } catch (SQLException e) {
            error(resp, out, 500, "Database error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    // ---- PUT /api/cart ----------------------------------------------------

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            JsonObject body = parseBody(req);

            if (!body.has("cartId") || !body.has("quantity")) {
                error(resp, out, 400, "cartId and quantity are required");
                return;
            }

            int cartId   = body.get("cartId").getAsInt();
            int quantity = body.get("quantity").getAsInt();

            if (quantity < 1) {
                error(resp, out, 400, "Quantity must be at least 1");
                return;
            }

            CartItem item = cartDAO.updateQuantity(cartId, quantity);
            out.print(gson.toJson(item));
        } catch (JsonSyntaxException e) {
            error(resp, out, 400, "Invalid JSON body");
        } catch (SQLException e) {
            error(resp, out, 500, "Database error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    // ---- DELETE /api/cart?cartId={id} -------------------------------------

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String raw = req.getParameter("cartId");
        if (raw == null || raw.isBlank()) {
            error(resp, out, 400, "cartId is required");
            return;
        }

        try {
            int cartId  = Integer.parseInt(raw);
            boolean ok  = cartDAO.removeFromCart(cartId);
            if (ok) {
                out.print(gson.toJson(Map.of("message", "Item removed from cart")));
            } else {
                error(resp, out, 404, "Cart item not found");
            }
        } catch (NumberFormatException e) {
            error(resp, out, 400, "cartId must be an integer");
        } catch (SQLException e) {
            error(resp, out, 500, "Database error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    // ---- helpers ----------------------------------------------------------

    private JsonObject parseBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return gson.fromJson(sb.toString(), JsonObject.class);
    }

    private void error(HttpServletResponse resp, PrintWriter out, int status, String msg) {
        resp.setStatus(status);
        out.print(gson.toJson(Map.of("error", msg)));
        out.flush();
    }
}
