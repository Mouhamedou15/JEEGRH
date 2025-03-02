package filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Utilisateur;

import java.io.IOException;

@WebFilter("/*") // Appliquer le filtre à toutes les requêtes
public class AuthFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String path = req.getRequestURI();

        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("utilisateur") : null;

        // 📌 Liste des pages protégées
        boolean isAdminPage = path.endsWith("admin.jsp");
        boolean isResponsablePage = path.endsWith("responsable.jsp");
        boolean isEmployePage = path.endsWith("employe.jsp");

        // 🚨 **Si l'utilisateur n'est pas connecté et tente d'accéder à une page protégée**
        if (user == null && (isAdminPage || isResponsablePage || isEmployePage)) {
            res.sendRedirect("error.jsp"); // Rediriger vers error.jsp
            return;
        }

        // 🚨 **Gérer les restrictions d'accès selon le rôle**
        if (user != null) {
            String role = user.getRole();

            // 📌 **L'Admin a accès à tout**
            if (role.equals("Admin")) {
                chain.doFilter(request, response);
                return;
            }

            // 🚨 **Restrictions pour Responsable**
            if (role.equals("Responsable") && isAdminPage) {
                res.sendRedirect("error.jsp"); // Rediriger vers error.jsp
                return;
            }

            // 🚨 **Restrictions pour Employé**
            if (role.equals("Employé") && (isAdminPage || isResponsablePage)) {
                res.sendRedirect("error.jsp"); // Rediriger vers error.jsp
                return;
            }
        }

        // ✅ Laisser passer la requête si tout est bon
        chain.doFilter(request, response);
    }
}
