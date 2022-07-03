package com.sadharan.indoor_positioning.surveyor;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class FloorCatalogueViewModel extends ViewModel {
    public long block_id;
    ArrayList<FloorElement> floorElements;
    private LocalSurveyDatabase localSurveyDatabase;

    public void setLocalSurveyDatabase(LocalSurveyDatabase localSurveyDatabase) {
        this.localSurveyDatabase = localSurveyDatabase;
    }

    public void updateCatalogue() {
        this.floorElements = localSurveyDatabase.getFloorList(this.block_id);
    }
}
