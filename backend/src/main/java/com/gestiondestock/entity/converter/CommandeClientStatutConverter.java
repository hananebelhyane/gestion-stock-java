package com.gestiondestock.entity.converter;

import com.gestiondestock.entity.CommandeClient;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CommandeClientStatutConverter implements AttributeConverter<CommandeClient.StatutCommande, String> {

    @Override
    public String convertToDatabaseColumn(CommandeClient.StatutCommande attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public CommandeClient.StatutCommande convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return CommandeClient.StatutCommande.valueOf(dbData.toLowerCase());
        } catch (IllegalArgumentException ex) {
            return CommandeClient.StatutCommande.en_attente;
        }
    }
}
