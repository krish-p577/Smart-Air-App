package com.SmartAir.glossary.model;

import java.util.ArrayList;
import java.util.List;

public class GlossaryRepository {

    public List<GlossaryItem> getGlossaryItems() {
        List<GlossaryItem> items = new ArrayList<>();
        items.add(new GlossaryItem("Asthma", "A chronic respiratory disease characterized by inflammation of the airways."));
        items.add(new GlossaryItem("Inhaler", "A medical device used for delivering medication into the body via the lungs."));
        items.add(new GlossaryItem("Controller Medication", "Medication taken daily to control asthma and prevent symptoms."));
        items.add(new GlossaryItem("Rescue Medication", "Medication used for quick relief of asthma symptoms."));
        items.add(new GlossaryItem("Trigger", "A substance or condition that can cause asthma symptoms to appear or worsen."));
        items.add(new GlossaryItem("PEF", "Peak Expiratory Flow, a measure of how fast a person can exhale."));
        return items;
    }
}
