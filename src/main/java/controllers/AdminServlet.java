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
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonRequest = acceptHeader != null && acceptHeader.contains("application/json");

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
        // ✅ Lire les paramètres du formulaire (HTML) ou de Postman (JSON)
        String email = request.getParameter("email");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String motDePasse = request.getParameter("password");
        String role = request.getParameter("role");
        String departementIdStr = request.getParameter("departement");

        Map<String, Object> jsonResponse = new HashMap<>();

        // ❌ Vérification des champs obligatoires
        if (email == null || nom == null || prenom == null || motDePasse == null || role == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Tous les champs sont obligatoires.");
            sendJsonResponse(response, jsonResponse);
            return;
        }

        // Vérifier si le département existe (sauf pour l'Admin qui n'a pas de département obligatoire)
        String departement = null;
        if (!role.equals("Admin") && departementIdStr != null) {
            try {
                int departementId = Integer.parseInt(departementIdStr);
                if (!departements.containsKey(departementId)) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Le département sélectionné n'existe pas.");
                    sendJsonResponse(response, jsonResponse);
                    return;
                }
                departement = departements.get(departementId);
            } catch (NumberFormatException e) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "ID de département invalide.");
                sendJsonResponse(response, jsonResponse);
                return;
            }
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

    // ✅ Méthode pour envoyer une réponse JSON propre
    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
