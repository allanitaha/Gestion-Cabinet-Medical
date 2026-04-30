ALTER TABLE rendezvous
    ADD CONSTRAINT unique_rdv UNIQUE (medecin_id, date_heure);

