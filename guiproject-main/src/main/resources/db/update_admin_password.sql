-- Mise à jour du mot de passe admin (mot de passe: admin123)
UPDATE utilisateur 
SET mot_de_passe = 'AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=' 
WHERE email = 'admin@example.com' AND type_utilisateur = 'ADMIN'; 