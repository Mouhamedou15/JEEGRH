<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %> <%-- ✅ Import de Map pour éviter l'erreur --%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Gestion des Utilisateurs</title>
    <style>
        /* ✅ Styles généraux */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            text-align: center;
        }

        h2 {
            color: #333;
        }

        /* ✅ Style du formulaire */
        form {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
            width: 40%;
            margin: auto;
        }

        label {
            display: block;
            font-weight: bold;
            margin: 10px 0 5px;
        }

        input, select {
            width: 100%;
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        button {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 10px;
            width: 100%;
            font-size: 16px;
            border-radius: 5px;
            cursor: pointer;
        }

        button:hover {
            background-color: #218838;
        }

        /* ✅ Messages de confirmation et d'erreur */
        .success {
            color: green;
            font-weight: bold;
            margin-top: 10px;
        }

        .error {
            color: red;
            font-weight: bold;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <h2>👤 Ajouter un Utilisateur</h2>

    <form action="admin" method="post">
        <label>Nom :</label>
        <input type="text" name="nom" required>

        <label>Prénom :</label>
        <input type="text" name="prenom" required>

        <label>Email :</label>
        <input type="email" name="email" required>

        <label>Mot de passe :</label>
        <input type="password" name="password" required>

        <label>Rôle :</label>
        <select name="role">
            <option value="Admin">Admin</option>
            <option value="Responsable">Responsable</option>
            <option value="Employé">Employé</option>
        </select>

        <label>Département :</label>
        <select name="departement">
            <option value="1">Informatique</option>
            <option value="2">Comptabilité</option>
            <option value="3">Gestion</option>
        </select>

        <button type="submit">➕ Ajouter Utilisateur</button>
    </form>

    <!-- ✅ Affichage du message de confirmation ou d'erreur -->
    <% String message = (String) request.getAttribute("message"); %>
    <% if (message != null) { %>
        <p class="<%= message.contains("succès") ? "success" : "error" %>"><%= message %></p>
    <% } %>
</body>
</html>
