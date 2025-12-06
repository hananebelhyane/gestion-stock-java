package com.gestiondestock.entity.converter;

import com.gestiondestock.entity.CommandeFournisseur;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CommandeFournisseurStatutConverter implements AttributeConverter<CommandeFournisseur.StatutCommande, String> {

    @Override
    public String convertToDatabaseColumn(CommandeFournisseur.StatutCommande attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public CommandeFournisseur.StatutCommande convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return CommandeFournisseur.StatutCommande.valueOf(dbData.toLowerCase());
        } catch (IllegalArgumentException ex) {
            // Fallback par défaut pour valeurs inattendues
            return CommandeFournisseur.StatutCommande.en_attente;
        }
    }
}
