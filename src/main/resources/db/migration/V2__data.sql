-- Insérer un médecin
INSERT INTO medecin (email, matricule, nom, prenom, specialite, telephone)
VALUES ('dr.sami@example.com', 'M98765', 'Sami', 'Ben Youssef', 'Cardiologue', '22111111');

-- Insérer un patient
INSERT INTO patient (cin, date_naissance, email, nom, prenom, telephone)
VALUES ('CIN456', '1985-03-20', 'mouna@example.com', 'Trabelsi', 'Mouna', '22122222');

-- Insérer une secrétaire
INSERT INTO secretaire (nom, prenom, email, telephone)
VALUES ('Leila', 'Haddad', 'leila@example.com', '22133333');

-- Insérer un rendezvous
INSERT INTO rendezvous (date_heure, motif, medecin_id, patient_id, secretaire_id)
VALUES ('2026-05-10 09:00:00', 'Contrôle cardiaque', 1, 1, 1);

-- Insérer une ordonnance
INSERT INTO ordonnance (date_emission, observations, rendezvous_id)
VALUES ('2026-05-10 09:30:00', 'Patient avec tension élevée', 1);

-- Insérer une ligne de médicament
INSERT INTO ligne_medicament (duree, nom_medicament, posologie, ordonnance_id)
VALUES ('14 jours', 'Amlodipine', '5mg une fois par jour', 1);

-- Créer les rôles
INSERT INTO roles (name) VALUES ('ADMIN'), ('MEDECIN'), ('SECRETAIRE');

-- Créer les utilisateurs
INSERT INTO users (username, password, enabled) VALUES
                                                    ('admin', '{noop}admin123', true),
                                                    ('medecin', '{noop}medecin123', true),
                                                    ('secretaire', '{noop}sec123', true);

-- Associer les rôles aux utilisateurs
INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1), -- admin → ADMIN
                                              (2, 2), -- medecin → MEDECIN
                                              (3, 3); -- secretaire → SECRETAIRE
