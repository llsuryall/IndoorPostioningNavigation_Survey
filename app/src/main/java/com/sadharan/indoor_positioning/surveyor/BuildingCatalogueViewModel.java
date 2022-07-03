package com.sadharan.indoor_positioning.surveyor;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class BuildingCatalogueViewModel extends ViewModel {
    ArrayList<BuildingElement> buildingElements;
    private LocalSurveyDatabase localSurveyDatabase;

    public void setLocalSurveyDatabase(LocalSurveyDatabase localSurveyDatabase) {
        this.localSurveyDatabase = localSurveyDatabase;
    }

    public void updateCatalogue() {
        this.buildingElements = localSurveyDatabase.getBuildingList();
    }
}
