package com.bookstore.servlet;
import com.bookstore.dao.*; import com.bookstore.model.*; import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import java.io.*; import java.util.*;
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private final UserDAO dao = new UserDAO(); private final Gson gson = new Gson();
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8"); JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
        String path = req.getPathInfo();
        try {
            if ("/register".equals(path)) {
                User u = new User(); u.setUsername(body.get("username").getAsString()); u.setEmail(body.get("email").getAsString()); u.setPassword(body.get("password").getAsString());
                if (dao.usernameExists(u.getUsername()) || dao.emailExists(u.getEmail())) { resp.setStatus(409); return; }
                resp.getWriter().print(gson.toJson(dao.register(u)));
            } else if ("/login".equals(path)) {
                User u = dao.login(body.get("username").getAsString(), body.get("password").getAsString());
                if (u == null) resp.setStatus(401); else resp.getWriter().print(gson.toJson(u));
            }
        } catch (Exception e) { resp.setStatus(500); }
    }
}
