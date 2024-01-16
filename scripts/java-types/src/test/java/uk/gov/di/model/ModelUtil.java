package uk.gov.di.model;

public final class ModelUtil {
    public static NamePartClass__4 buildNamePart(NamePartClass__4.NamePartType type, String name) {
        var namePart = new NamePartClass__4();
        namePart.setType(type);
        namePart.setValue(name);
        return namePart;
    }
}
