package filters;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

@WebFilter("/*") // Appliquer le filtre à toutes les requêtes
public class AuthFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String path = req.getRequestURI();

        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("utilisateur") : null;

        // 📌 Détecter si la requête vient d'une API (Postman)
        boolean isApiRequest = req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json");

        // 📌 Vérifier si un Token est fourni pour Postman
        String authHeader = req.getHeader("Authorization");
        boolean hasValidToken = authHeader != null && authHeader.startsWith("Bearer ");

        // 📌 Liste des pages protégées
        boolean isProtectedPage = path.endsWith("admin.jsp") || path.endsWith("responsable.jsp") || path.endsWith("employe.jsp");

        // 🚨 **Si l'utilisateur n'est pas connecté et tente d'accéder à une page protégée**
        if (user == null && isProtectedPage) {
            if (isApiRequest) {
                if (!hasValidToken) {
                    sendJsonError(res, "Accès refusé. Token d'authentification requis.");
                    return;
                }
            } else {
                res.sendRedirect("error.jsp"); // Rediriger vers error.jsp
                return;
            }
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
            if (role.equals("Responsable") && path.endsWith("admin.jsp")) {
                if (isApiRequest) {
                    sendJsonError(res, "Accès refusé. Un responsable ne peut pas accéder à la page Admin.");
                } else {
                    res.sendRedirect("error.jsp");
                }
                return;
            }

            // 🚨 **Restrictions pour Employé**
            if (role.equals("Employé") && (path.endsWith("admin.jsp") || path.endsWith("responsable.jsp"))) {
                if (isApiRequest) {
                    sendJsonError(res, "Accès refusé. Un employé ne peut pas accéder à ces pages.");
                } else {
                    res.sendRedirect("error.jsp");
                }
                return;
            }
        }

        // ✅ Laisser passer la requête si tout est bon
        chain.doFilter(request, response);
    }

    // ✅ Méthode pour envoyer une réponse JSON propre en cas d'erreur
    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("status", "error");
        jsonResponse.put("message", message);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
