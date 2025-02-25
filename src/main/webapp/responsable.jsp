<%@ page import="models.Utilisateur" %>
<%@ page session="true" %>

<%
    Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");

    if (user == null) {
        response.sendRedirect("index.html"); // Redirection si l'utilisateur n'est pas connecté
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil</title>
</head>
<body>

<h2>Bienvenue, <%= user.getNom() %> (<%= user.getRole() %>)</h2>
<p>Email : <%= user.getEmail() %></p>

<a href="logout">Se déconnecter</a>

</body>
</html>
