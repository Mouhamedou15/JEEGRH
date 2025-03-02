package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Utilisateur;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Stockage temporaire des utilisateurs en mémoire
    private static final Map<String, Utilisateur> utilisateurs = new HashMap<>();

    // Stockage temporaire des départements
    private static final Map<Integer, String> departements = new HashMap<>();

    static {
        // Ajout de quelques départements par défaut
        departements.put(1, "Informatique");
        departements.put(2, "Comptabilité");
        departements.put(3, "Gestion");
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ✅ Détecter si la requête attend du JSON ou HTML
        boolean isJsonRequest = "application/json".equals(request.getContentType());

        // ✅ Vérifier la méthode de requête
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method)) {
            ajouterUtilisateur(request, response, isJsonRequest);
        } else {
            // ✅ Si GET, afficher la page admin avec la liste des départements
            request.setAttribute("departements", departements);
            request.getRequestDispatcher("admin.jsp").forward(request, response);
        }
    }

    private void ajouterUtilisateur(HttpServletRequest request, HttpServletResponse response, boolean isJsonRequest) throws IOException, ServletException {
        String email = null, nom = null, prenom = null, motDePasse = null, role = null, departement = null;

        if (isJsonRequest) {
            // ✅ Lecture des données JSON envoyées dans le body (Postman)
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> jsonMap = objectMapper.readValue(request.getReader(), Map.class);
            email = jsonMap.get("email");
            nom = jsonMap.get("nom");
            prenom = jsonMap.get("prenom");
            motDePasse = jsonMap.get("password");
            role = jsonMap.get("role");
            String departementIdStr = jsonMap.get("departement");
            
            departement = validerDepartement(role, departementIdStr);
        } else {
            // ✅ Lecture des données envoyées en x-www-form-urlencoded (Formulaire HTML)
            email = request.getParameter("email");
            nom = request.getParameter("nom");
            prenom = request.getParameter("prenom");
            motDePasse = request.getParameter("password");
            role = request.getParameter("role");
            String departementIdStr = request.getParameter("departement");

            departement = validerDepartement(role, departementIdStr);
        }

        Map<String, Object> jsonResponse = new HashMap<>();

        // ❌ Vérification des champs obligatoires
        if (email == null || nom == null || prenom == null || motDePasse == null || role == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Tous les champs sont obligatoires.");
            sendJsonResponse(response, jsonResponse);
            return;
        }

        // ❌ Vérifier si l'email est déjà utilisé
        if (utilisateurs.containsKey(email)) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Cet email est déjà utilisé.");
            sendJsonResponse(response, jsonResponse);
            return;
        }

        // ✅ Création et ajout de l'utilisateur
        Utilisateur newUser = new Utilisateur(utilisateurs.size() + 1, nom, prenom, email, motDePasse, role, departement);
        utilisateurs.put(email, newUser);

        if (isJsonRequest) {
            // ✅ Réponse JSON pour Postman
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Utilisateur ajouté avec succès.");
            jsonResponse.put("data", newUser);
            sendJsonResponse(response, jsonResponse);
        } else {
            // ✅ Interface graphique : Redirection avec message de succès
            request.setAttribute("message", "✅ Utilisateur ajouté avec succès !");
            request.getRequestDispatcher("admin.jsp").forward(request, response);
        }
    }

    // ✅ Validation du département
    private String validerDepartement(String role, String departementIdStr) {
        if (role.equals("Admin")) {
            return ""; // ✅ L'Admin n'a pas de département
        }

        if (departementIdStr != null) {
            try {
                int departementId = Integer.parseInt(departementIdStr);
                if (departements.containsKey(departementId)) {
                    return departements.get(departementId);
                }
            } catch (NumberFormatException ignored) {}
        }
        return null; // 🚨 Si invalide, retournera une erreur
    }

    // ✅ Méthode pour envoyer une réponse JSON propre
    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
