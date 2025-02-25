package filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Utilisateur;

import java.io.IOException;

@WebFilter("/*") // Appliquer le filtre à toutes les pages
public class AuthFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String path = req.getRequestURI();

        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("utilisateur") : null;

        // Liste des pages protégées
        boolean isAdminPage = path.endsWith("admin.jsp");
        boolean isResponsablePage = path.endsWith("responsable.jsp");
        boolean isEmployePage = path.endsWith("employe.jsp");

        // Si l'utilisateur n'est pas connecté et essaie d'accéder à une page protégée
        if (user == null && (isAdminPage || isResponsablePage || isEmployePage)) {
            res.sendRedirect("index.html"); // Redirection vers la page de connexion
            return;
        }

        // Vérification des accès selon le rôle utilisateur
        if (user != null) {
            String role = user.getRole();

            if (role.equals("Admin") && isResponsablePage) {
                res.sendRedirect("error.jsp"); // Admin ne peut pas accéder à responsable.jsp
                return;
            }
            if (role.equals("Responsable") && isAdminPage) {
                res.sendRedirect("error.jsp"); // Responsable ne peut pas accéder à admin.jsp
                return;
            }
            if (role.equals("Employé") && (isAdminPage || isResponsablePage)) {
                res.sendRedirect("error.jsp"); // Employé ne peut pas accéder aux pages Admin/Responsable
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
