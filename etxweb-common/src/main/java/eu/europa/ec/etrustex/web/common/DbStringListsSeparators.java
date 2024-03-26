package eu.europa.ec.etrustex.web.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DbStringListsSeparators {

    DB_STRING_LIST_SEPARATOR(","),
    DISPLAY_STRING_LIST_SEPARATOR(", ");


    private final String value;

    DbStringListsSeparators(String value) {
        this.value=value;
    }
    @Override
    @JsonValue
    public String toString() {
        return this.value;
    }

}
