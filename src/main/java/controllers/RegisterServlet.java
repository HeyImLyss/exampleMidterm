package controllers;


import org.example.samplehash.entities.UserInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@WebServlet(name = "RegisterServlet", value = "/register-salted")
public class RegisterServlet extends HttpServlet {
    private EntityManagerFactory emf;

    @Override
    public void init() {
        emf = Persistence.createEntityManagerFactory("SecureAuthPU");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String rawPassword = request.getParameter("password");

        try {
            // 1. Generate a secure random salt (16 bytes)
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);

            // 2. Combine password + salt and hash using SHA-256
            String passwordAndSalt = rawPassword + salt;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(passwordAndSalt.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            String finalHash = sb.toString();

            // 3. Persist to database using JPA
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            UserInfo user = new UserInfo(username, finalHash, salt);
            em.persist(user);
            em.getTransaction().commit();
            em.close();

            // 4. Output success response
            response.setContentType("text/html");
            response.getWriter().println("<h3>Success! User registered with a unique salt.</h3>");
            response.getWriter().println("<p><b>Stored Salt:</b> " + salt + "</p>");
            response.getWriter().println("<p><b>Stored Hash:</b> " + finalHash + "</p>");

        } catch (Exception e) {
            throw new ServletException("Registration failed", e);
        }
    }

    @Override
    public void destroy() {
        if (emf != null) emf.close();
    }
}