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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Utilisateur user = utilisateurs.get(email);

        if (user != null && user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", user);

            // Redirection automatique selon le rôle
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
        } else {
            // Stocker le message d'erreur dans la session pour qu'il soit visible après la redirection
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "⚠️ Email ou mot de passe incorrect !");
            response.sendRedirect("index.jsp");
        }
    }
}
