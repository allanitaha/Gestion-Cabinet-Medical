-- Table Patient
CREATE TABLE patient (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         cin VARCHAR(20) UNIQUE NOT NULL,
                         date_naissance DATE,
                         email VARCHAR(100),
                         nom VARCHAR(100),
                         prenom VARCHAR(100),
                         telephone VARCHAR(20)
);

-- Table Médecin
CREATE TABLE medecin (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         email VARCHAR(100),
                         matricule VARCHAR(50) UNIQUE NOT NULL,
                         nom VARCHAR(100),
                         prenom VARCHAR(100),
                         specialite VARCHAR(100),
                         telephone VARCHAR(20)
);

-- Table Secrétaire
CREATE TABLE secretaire (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nom VARCHAR(100),
                            prenom VARCHAR(100),
                            email VARCHAR(100),
                            telephone VARCHAR(20)
);

-- Table Rendezvous (unique)
CREATE TABLE rendezvous (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            date_heure TIMESTAMP NOT NULL,
                            motif VARCHAR(255) NOT NULL,
                            medecin_id BIGINT NOT NULL,
                            patient_id BIGINT NOT NULL,
                            secretaire_id BIGINT,
                            FOREIGN KEY (medecin_id) REFERENCES medecin(id),
                            FOREIGN KEY (patient_id) REFERENCES patient(id),
                            FOREIGN KEY (secretaire_id) REFERENCES secretaire(id)
);

-- Table Ordonnance
CREATE TABLE ordonnance (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            date_emission TIMESTAMP,
                            observations TEXT,
                            rendezvous_id BIGINT,
                            FOREIGN KEY (rendezvous_id) REFERENCES rendezvous(id)
);

-- Table Ligne Médicament
CREATE TABLE ligne_medicament (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  duree VARCHAR(100),
                                  nom_medicament VARCHAR(100),
                                  posologie VARCHAR(100),
                                  ordonnance_id BIGINT,
                                  FOREIGN KEY (ordonnance_id) REFERENCES ordonnance(id)
);

-- Table Utilisateurs
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN DEFAULT true
);

CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
                            user_id BIGINT,
                            role_id BIGINT,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (role_id) REFERENCES roles(id)
);

