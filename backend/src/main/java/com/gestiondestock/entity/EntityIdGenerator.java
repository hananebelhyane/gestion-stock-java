package com.gestiondestock.entity;

import jakarta.persistence.PrePersist;
import java.util.UUID;
import java.lang.reflect.Field;

public class EntityIdGenerator {

    @PrePersist
    public void generateId(Object entity) {
        try {
            // Trouver le champ 'id' dans l'entité
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);

            // Si l'ID est null, générer un UUID
            if (idField.get(entity) == null) {
                idField.set(entity, UUID.randomUUID());
            }
        } catch (Exception e) {
            // Ignorer si le champ n'existe pas ou n'est pas accessible
        }
    }
}
