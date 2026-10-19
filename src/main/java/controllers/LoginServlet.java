package controllers;

import org.example.samplehash.entities.UserInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.MessageDigest;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
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

        EntityManager em = emf.createEntityManager();
        try {
            // 1. Query user by username
            TypedQuery<UserInfo> query = em.createQuery(
                    "SELECT u FROM UserInfo u WHERE u.username = :username", UserInfo.class);
            query.setParameter("username", username);

            UserInfo user = query.getSingleResult();

            // 2. Retrieve user's stored salt and hash
            String salt = user.getSalt();
            String storedHash = user.getPasswordHash();

            // 3. Hash the entered password with the user's specific salt using SHA-256
            String passwordAndSalt = rawPassword + salt;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(passwordAndSalt.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            String computedHash = sb.toString();

            // 4. Compare computed hash with stored hash
            response.setContentType("text/html");
            if (computedHash.equals(storedHash)) {
                response.getWriter().println("<h3 style='color:green;'>✅ Success! Correct password. Welcome, " + user.getUsername() + "!</h3>");
            } else {
                response.getWriter().println("<h3 style='color:red;'>❌ Wrong password. Please try again.</h3>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3 style='color:red;'>❌ User not found or login failed.</h3>");
        } finally {
            em.close();
        }
    }

    @Override
    public void destroy() {
        if (emf != null) emf.close();
    }
}