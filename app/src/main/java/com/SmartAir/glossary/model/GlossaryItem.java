package com.SmartAir.glossary.model;

public class GlossaryItem {
    private String term;
    private String definition;

    public GlossaryItem(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }
}
