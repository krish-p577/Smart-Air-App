package com.SmartAir.glossary.presenter;

import com.SmartAir.glossary.model.GlossaryItem;
import com.SmartAir.glossary.model.GlossaryRepository;

import java.util.List;

public class GlossaryPresenter {

    private View view;
    private GlossaryRepository repository;

    public interface View {
        void showGlossary(List<GlossaryItem> items);
        void showError(String message);
    }

    public GlossaryPresenter(View view) {
        this.view = view;
        this.repository = new GlossaryRepository();
    }

    public void loadGlossary() {
        view.showGlossary(repository.getGlossaryItems());
    }
}
