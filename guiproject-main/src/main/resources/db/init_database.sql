-- Création de la base de données
CREATE DATABASE IF NOT EXISTS guiproject;
USE guiproject;

-- Création de la table utilisateur
CREATE TABLE IF NOT EXISTS utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    type_utilisateur VARCHAR(20) NOT NULL,
    preferences TEXT,
    CONSTRAINT chk_type_utilisateur CHECK (type_utilisateur IN ('ADMIN', 'USER', 'MANAGER'))
);

-- Insertion d'un utilisateur admin par défaut (mot de passe: admin123)
INSERT INTO utilisateur (nom, email, mot_de_passe, type_utilisateur, preferences)
VALUES ('Administrateur', 'admin@example.com', '$2a$10$rDkPvvAFV6GgJjXpzXqYQOQZqZqZqZqZqZqZqZqZqZqZqZqZqZq', 'ADMIN', '{"theme": "dark"}')
ON DUPLICATE KEY UPDATE nom = nom; 