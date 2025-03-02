package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Utilisateur;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Simuler une base de données en mémoire
    private static final Map<String, Utilisateur> utilisateurs = new HashMap<>();

    static {
        utilisateurs.put("admin@company.com", new Utilisateur(1, "Admin", "User", "admin@company.com", "admin", "Admin"));
        utilisateurs.put("responsable@company.com", new Utilisateur(2, "Responsable", "User", "responsable@company.com", "resp123", "Responsable"));
        utilisateurs.put("employe@company.com", new Utilisateur(3, "Employé", "User", "employe@company.com", "emp123", "Employé"));
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 🔹 Détecter si la requête attend JSON (Postman)
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonRequest = acceptHeader != null && acceptHeader.contains("application/json");

        // 🔹 Récupérer les paramètres de connexion
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Utilisateur user = utilisateurs.get(email);

        // 🔹 Préparer l'objet JSON de réponse
        Map<String, Object> jsonResponse = new HashMap<>();

        if (user != null && user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", user);

            if (isJsonRequest) {
                // ✅ Réponse JSON pour Postman
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Authentification réussie");
                Map<String, String> userData = new HashMap<>();
                userData.put("email", user.getEmail());
                userData.put("role", user.getRole());
                jsonResponse.put("data", userData);
                sendJsonResponse(response, jsonResponse);
            } else {
                // ✅ Redirection pour l’interface graphique (Navigateur)
                switch (user.getRole()) {
                    case "Admin":
                        response.sendRedirect("admin.jsp");
                        break;
                    case "Responsable":
                        response.sendRedirect("responsable.jsp");
                        break;
                    case "Employé":
                        response.sendRedirect("employe.jsp");
                        break;
                    default:
                        response.sendRedirect("error.jsp");
                        break;
                }
            }
        } else {
            if (isJsonRequest) {
                // ❌ Réponse JSON si erreur de connexion via Postman
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Email ou mot de passe incorrect");
                sendJsonResponse(response, jsonResponse);
            } else {
                // ❌ Message d’erreur pour l’interface graphique
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "⚠️ Email ou mot de passe incorrect !");
                response.sendRedirect("index.jsp");
            }
        }
    }

    // 🔹 Méthode utilitaire pour envoyer une réponse JSON propre
    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
