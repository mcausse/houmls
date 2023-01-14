package org.homs.houmls.shape.impl.connector;

public enum Type {

    DEFAULT(""),
    ARROW("<"),
    INHERITANCE("<<"),
    INHERITANCE_BLACKFILLED("<<<"),
    AGGREGATION("<<<<"),
    COMPOSITION("<<<<<"),
    MEMBER_COMMENT("m"),
    //
    // Crow’s Foot Notation
    // https://vertabelo.com/blog/crow-s-foot-notation/
    // http://www2.cs.uregina.ca/~bernatja/crowsfoot.html
    //
    TO_ONE_OPTIONAL("|o"), TO_ONE_MANDATORY("||"),
    TO_MANY_OPTIONAL(">o"), TO_MANY_MANDATORY(">|"),

    REQUIRED(")"), PROVIDED("o"),

    INNER_CLASS("+");

    private final String code;

    Type(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Type findByCode(String code) {
        for (Type type : Type.values()) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return DEFAULT;
    }
}