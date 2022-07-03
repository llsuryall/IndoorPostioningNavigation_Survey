package com.sadharan.indoor_positioning.surveyor;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class BlockCatalogueViewModel extends ViewModel {
    public long building_id;
    ArrayList<BlockElement> blockElements;
    private LocalSurveyDatabase localSurveyDatabase;

    public void setLocalSurveyDatabase(LocalSurveyDatabase localSurveyDatabase) {
        this.localSurveyDatabase = localSurveyDatabase;
    }

    public void updateCatalogue() {
        this.blockElements = localSurveyDatabase.getBlockList(this.building_id);
    }
}
