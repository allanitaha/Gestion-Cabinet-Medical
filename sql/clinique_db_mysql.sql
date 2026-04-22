-- Gestion Cabinet — schéma MySQL (aligné sur les entités JPA + naming snake_case par défaut)
-- Exécution : mysql -u root -p < sql/clinique_db_mysql.sql
-- (ou coller le contenu dans MySQL Workbench)

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS clinique_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE clinique_db;

DROP TABLE IF EXISTS ligne_medicaments;
DROP TABLE IF EXISTS ordonnances;
DROP TABLE IF EXISTS rendez_vous;
DROP TABLE IF EXISTS medecins;
DROP TABLE IF EXISTS patients;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE patients (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cin VARCHAR(32) NOT NULL,
  nom VARCHAR(80) NOT NULL,
  prenom VARCHAR(80) NOT NULL,
  date_naissance DATE NULL,
  telephone VARCHAR(30) NULL,
  email VARCHAR(120) NULL,
  antecedents TEXT NULL,
  date_creation DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_patient_cin (cin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE medecins (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nom VARCHAR(80) NOT NULL,
  prenom VARCHAR(80) NOT NULL,
  specialite VARCHAR(120) NULL,
  numero_ordre VARCHAR(64) NOT NULL,
  telephone VARCHAR(30) NULL,
  email VARCHAR(120) NULL,
  actif TINYINT(1) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_medecin_numero_ordre (numero_ordre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rendez_vous (
  id BIGINT NOT NULL AUTO_INCREMENT,
  date_heure DATETIME(6) NOT NULL,
  duree_minutes INT NOT NULL,
  statut VARCHAR(20) NOT NULL,
  motif VARCHAR(255) NULL,
  patient_id BIGINT NOT NULL,
  medecin_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_rdv_patient (patient_id),
  KEY idx_rdv_medecin (medecin_id),
  CONSTRAINT fk_rdv_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
  CONSTRAINT fk_rdv_medecin FOREIGN KEY (medecin_id) REFERENCES medecins (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ordonnances (
  id BIGINT NOT NULL AUTO_INCREMENT,
  date_emission DATE NOT NULL,
  observations TEXT NULL,
  rendez_vous_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_ordonnance_rendez_vous (rendez_vous_id),
  CONSTRAINT fk_ordonnance_rdv FOREIGN KEY (rendez_vous_id) REFERENCES rendez_vous (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ligne_medicaments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nom_medicament VARCHAR(160) NOT NULL,
  posologie VARCHAR(255) NULL,
  duree VARCHAR(120) NULL,
  ordonnance_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_ligne_ordonnance (ordonnance_id),
  CONSTRAINT fk_ligne_ordonnance FOREIGN KEY (ordonnance_id) REFERENCES ordonnances (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
