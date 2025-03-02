package controllers;

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
        // 🔹 Récupérer les paramètres de la requête (valide pour toutes les méthodes)
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Utilisateur user = utilisateurs.get(email);

        // 🔹 Vérifier si la requête vient d'une API (Postman)
        boolean isApiRequest = "application/json".equals(request.getHeader("Accept"));

        if (user != null && user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", user);

            if (isApiRequest) {
                // ✅ Réponse JSON pour Postman
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\":\"Connexion réussie\", \"role\":\"" + user.getRole() + "\"}");
            } else {
                // ✅ Redirection selon le rôle pour l'interface graphique
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
            if (isApiRequest) {
                // ❌ Réponse JSON si échec de connexion via API
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\":\"Email ou mot de passe incorrect\"}");
            } else {
                // ❌ Message d'erreur pour l'interface web
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "⚠️ Email ou mot de passe incorrect !");
                response.sendRedirect("index.jsp");
            }
        }
    }
}
