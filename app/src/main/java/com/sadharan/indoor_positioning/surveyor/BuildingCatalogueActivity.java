package com.sadharan.indoor_positioning.surveyor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class BuildingCatalogueActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener, View.OnLongClickListener {
    private LocalSurveyDatabase localSurveyDatabase;
    private BuildingCatalogueViewModel buildingCatalogueViewModel;
    private AlertDialog addBuildingAlertDialog;
    private LinearLayout buildingElementsHolder;
    private View addBuildingContent;
    private LayoutInflater layoutInflater;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.building_catalogue_activity);
        this.localSurveyDatabase = new LocalSurveyDatabase(getApplicationContext());
        this.buildingCatalogueViewModel = new ViewModelProvider(this).get(BuildingCatalogueViewModel.class);
        this.buildingCatalogueViewModel.setLocalSurveyDatabase(localSurveyDatabase);
        if (this.buildingCatalogueViewModel.buildingElements == null) {
            this.buildingCatalogueViewModel.updateCatalogue();
        }
        AlertDialog.Builder addBuildingAlertBoxBuilder;
        addBuildingAlertBoxBuilder = new AlertDialog.Builder(BuildingCatalogueActivity.this);
        this.layoutInflater = LayoutInflater.from(BuildingCatalogueActivity.this);
        this.addBuildingContent = layoutInflater.inflate(R.layout.add_building_dialog, null);
        addBuildingAlertBoxBuilder.setTitle(R.string.add_building)
                .setPositiveButton(R.string.save_button, this)
                .setNegativeButton(R.string.cancel_button, null)
                .setView(addBuildingContent)
                .setCancelable(false);
        this.addBuildingAlertDialog = addBuildingAlertBoxBuilder.create();
        this.buildingElementsHolder = findViewById(R.id.building_elements_holder);
        findViewById(R.id.addBuildingButton).setOnClickListener(this);
        findViewById(R.id.resetDatabaseButton).setOnClickListener(this);
        refreshCatalogueView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addBuildingButton) {
            this.addBuildingAlertDialog.show();
        } else if (view.getId() == R.id.resetDatabaseButton) {
            boolean successful = false;
            try {
                successful = this.localSurveyDatabase.resetDatabase();
                buildingCatalogueViewModel.updateCatalogue();
                refreshCatalogueView();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (successful) {
                Toast.makeText(this, R.string.reset_database_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.reset_database_failure, Toast.LENGTH_SHORT).show();
            }
        } else {
            long building_id = Long.parseLong(((TextView) view.findViewById(R.id.building_id)).getText().toString());
            Intent blockCatalogueActivity = new Intent(this, BlockCatalogueActivity.class);
            blockCatalogueActivity.putExtra(getString(R.string.building_id_field), building_id);
            startActivity(blockCatalogueActivity);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        try {
            if (this.localSurveyDatabase.deleteBuilding(Long.parseLong(((TextView) view.findViewById(R.id.building_id)).getText().toString()))) {
                Toast.makeText(this, R.string.delete_building_successful, Toast.LENGTH_SHORT).show();
                buildingCatalogueViewModel.updateCatalogue();
                refreshCatalogueView();
            } else {
                Toast.makeText(this, R.string.delete_building_failure, Toast.LENGTH_SHORT).show();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        EditText buildingNameInput = this.addBuildingContent.findViewById(R.id.building_name_input);
        EditText buildingAddressInput = this.addBuildingContent.findViewById(R.id.building_address_input);
        String building_name = buildingNameInput.getText().toString(),
                building_address = buildingAddressInput.getText().toString();
        boolean successful;
        try {
            successful = localSurveyDatabase.addBuilding(new BuildingElement(-1, building_name, building_address));
        } catch (Exception e) {
            successful = false;
        }
        if (successful) {
            buildingCatalogueViewModel.updateCatalogue();
            refreshCatalogueView();
            buildingNameInput.setText("");
            buildingAddressInput.setText("");
        } else {
            Toast.makeText(getApplicationContext(), R.string.add_building_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshCatalogueView() {
        buildingElementsHolder.removeAllViews();
        for (BuildingElement buildingElement : this.buildingCatalogueViewModel.buildingElements) {
            addBuildingElement(buildingElement);
        }
    }

    private void addBuildingElement(BuildingElement buildingElement) {
        LinearLayout buildingElementView = (LinearLayout) this.layoutInflater.inflate(R.layout.building_element, buildingElementsHolder, false);
        buildingElementView.setOnClickListener(this);
        buildingElementView.setOnLongClickListener(this);
        String building_id_string = Long.toString(buildingElement.id);
        ((TextView) buildingElementView.findViewById(R.id.building_id)).setText(building_id_string);
        ((TextView) buildingElementView.findViewById(R.id.building_name)).setText(buildingElement.name);
        ((TextView) buildingElementView.findViewById(R.id.building_address)).setText(buildingElement.address);
        buildingElementsHolder.addView(buildingElementView);
    }
}
