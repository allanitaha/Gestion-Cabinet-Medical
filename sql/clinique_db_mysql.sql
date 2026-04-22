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
DROP TABLE IF EXISTS creneaux;
DROP TABLE IF EXISTS rendez_vous;
DROP TABLE IF EXISTS users;
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

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(80) NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  role VARCHAR(20) NOT NULL,
  enabled TINYINT(1) NOT NULL,
  patient_id BIGINT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username),
  UNIQUE KEY uk_user_patient (patient_id),
  CONSTRAINT fk_user_patient FOREIGN KEY (patient_id) REFERENCES patients (id)
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

CREATE TABLE creneaux (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medecin_id BIGINT NOT NULL,
  date_heure DATETIME(6) NOT NULL,
  duree_minutes INT NOT NULL,
  disponible TINYINT(1) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_creneau_medecin_date (medecin_id, date_heure),
  KEY idx_creneau_medecin (medecin_id),
  CONSTRAINT fk_creneau_medecin FOREIGN KEY (medecin_id) REFERENCES medecins (id)
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

-- =============================
-- Donnees de test logiques
-- =============================
INSERT INTO patients (cin, nom, prenom, date_naissance, telephone, email, antecedents, date_creation) VALUES
('AA123456', 'Benali', 'Sara', '1994-05-14', '0600000001', 'sara.benali@example.com', 'Allergie legere aux penicillines', NOW()),
('BB223344', 'El Idrissi', 'Yassine', '1988-11-03', '0600000002', 'yassine.elidrissi@example.com', 'Hypertension suivie', NOW()),
('CC556677', 'Khaldi', 'Meriem', '2000-02-20', '0600000003', 'meriem.khaldi@example.com', 'Aucun antecedent majeur', NOW());

INSERT INTO medecins (nom, prenom, specialite, numero_ordre, telephone, email, actif) VALUES
('Amrani', 'Hicham', 'Cardiologie', 'ORD-1001', '0700000001', 'hicham.amrani@clinique.ma', 1),
('Lahlou', 'Nadia', 'Medecine Generale', 'ORD-1002', '0700000002', 'nadia.lahlou@clinique.ma', 1);

INSERT INTO creneaux (medecin_id, date_heure, duree_minutes, disponible) VALUES
(1, '2026-05-05 09:00:00', 30, 1),
(1, '2026-05-05 09:30:00', 30, 1),
(1, '2026-05-06 10:00:00', 30, 1),
(2, '2026-05-05 11:00:00', 30, 1),
(2, '2026-05-06 14:00:00', 30, 1);

INSERT INTO rendez_vous (date_heure, duree_minutes, statut, motif, patient_id, medecin_id) VALUES
('2026-04-18 10:00:00', 30, 'PLANIFIE', 'Controle tension arterielle', 2, 1),
('2026-04-19 11:30:00', 45, 'TERMINE', 'Consultation annuelle', 1, 2);

INSERT INTO ordonnances (date_emission, observations, rendez_vous_id) VALUES
('2026-04-19', 'Traitement de 7 jours et repos', 2);

INSERT INTO ligne_medicaments (nom_medicament, posologie, duree, ordonnance_id) VALUES
('Paracetamol 500mg', '1 comprime matin et soir', '7 jours', 1),
('Vitamine D', '1 ampoule par semaine', '4 semaines', 1);
