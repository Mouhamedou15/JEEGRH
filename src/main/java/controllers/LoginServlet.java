package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Utilisateur;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Simuler une base de données en mémoire
    private static final Map<String, Utilisateur> utilisateurs = new HashMap<>();

    static {
        utilisateurs.put("admin@company.com", new Utilisateur(1, "Admin", "User", "admin@company.com", "admin", "Admin", "")); // ✅ Admin n'a pas de département
        utilisateurs.put("responsable@company.com", new Utilisateur(2, "Responsable", "User", "responsable@company.com", "resp123", "Responsable", "Ressources Humaines"));
        utilisateurs.put("employe@company.com", new Utilisateur(3, "Employé", "User", "employe@company.com", "emp123", "Employé", "Informatique"));
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 🔹 Détecter si la requête est en JSON ou x-www-form-urlencoded
        boolean isJsonRequest = "application/json".equals(request.getContentType());

        // 🔹 Récupérer les paramètres de connexion
        String email = null, password = null;

        if (isJsonRequest) {
            // ✅ Lire les données JSON depuis le corps de la requête
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> jsonMap = objectMapper.readValue(request.getReader(), Map.class);
            email = jsonMap.get("email");
            password = jsonMap.get("password");
        } else {
            // ✅ Lire les données depuis les paramètres du formulaire HTML (x-www-form-urlencoded)
            email = request.getParameter("email");
            password = request.getParameter("password");
        }

        Utilisateur user = utilisateurs.get(email);
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
                userData.put("departement", user.getDepartement().isEmpty() ? "Aucun" : user.getDepartement());

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

    // 🔹 Méthode pour envoyer une réponse JSON propre
    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
