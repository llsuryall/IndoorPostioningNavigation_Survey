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

public class FloorCatalogueActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener, View.OnLongClickListener {
    private LocalSurveyDatabase localSurveyDatabase;
    private FloorCatalogueViewModel floorCatalogueViewModel;
    private AlertDialog addFloorAlertDialog;
    private LayoutInflater layoutInflater;
    private LinearLayout floorElementsHolder;
    private EditText floorIDInput, floorHeightInput, floorWidthInput;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.floor_catalogue_activity);
        this.localSurveyDatabase = new LocalSurveyDatabase(getApplicationContext());
        this.floorCatalogueViewModel = new ViewModelProvider(this).get(FloorCatalogueViewModel.class);
        AlertDialog.Builder addFloorAlertBoxBuilder;
        addFloorAlertBoxBuilder = new AlertDialog.Builder(FloorCatalogueActivity.this);
        this.layoutInflater = LayoutInflater.from(FloorCatalogueActivity.this);
        View addFloorContent = this.layoutInflater.inflate(R.layout.add_floor_dialog, null);
        addFloorAlertBoxBuilder.setTitle(R.string.add_floor)
                .setPositiveButton(R.string.save_button, this)
                .setNegativeButton(R.string.cancel_button, null)
                .setView(addFloorContent)
                .setCancelable(false);
        this.addFloorAlertDialog = addFloorAlertBoxBuilder.create();
        this.floorElementsHolder = findViewById(R.id.floor_elements_holder);
        this.floorCatalogueViewModel.setLocalSurveyDatabase(localSurveyDatabase);
        if (getIntent().getLongExtra(getString(R.string.block_id_field), -1) >= 0) {
            this.floorCatalogueViewModel.block_id = getIntent().getLongExtra(getString(R.string.block_id_field), -1);
        }
        getIntent().removeExtra(getString(R.string.block_id_field));
        findViewById(R.id.addFloorButton).setOnClickListener(this);
        floorIDInput = addFloorContent.findViewById(R.id.floor_id_input);
        floorHeightInput = addFloorContent.findViewById(R.id.floor_height_input);
        floorWidthInput = addFloorContent.findViewById(R.id.floor_width_input);
        if (floorCatalogueViewModel.floorElements == null) {
            floorCatalogueViewModel.updateCatalogue();
        }
        refreshCatalogueView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addFloorButton) {
            this.addFloorAlertDialog.show();
        } else {
            Intent surveyActivity = new Intent(this, SurveyActivity.class);
            surveyActivity.putExtra(getString(R.string.block_id_field), this.floorCatalogueViewModel.block_id);
            surveyActivity.putExtra(getString(R.string.floor_id_field), Integer.parseInt(((TextView) view.findViewById(R.id.floor_id)).getText().toString()));
            startActivity(surveyActivity);
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int floor_id = Integer.parseInt(floorIDInput.getText().toString());
        float floor_height, floor_width;
        try {
            floor_height = Float.parseFloat(floorHeightInput.getText().toString());
            floor_width = Float.parseFloat(floorWidthInput.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.add_floor_failure, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean successful;
        try {
            successful = localSurveyDatabase.addFloor(new FloorElement(floor_id, this.floorCatalogueViewModel.block_id, floor_height, floor_width));
        } catch (Exception e) {
            successful = false;
        }
        if (successful) {
            Toast.makeText(getApplicationContext(), R.string.add_floor_successful, Toast.LENGTH_SHORT).show();
            floorCatalogueViewModel.updateCatalogue();
            refreshCatalogueView();
            floorIDInput.setText("");
            floorHeightInput.setText("");
            floorWidthInput.setText("");
        } else {
            Toast.makeText(getApplicationContext(), R.string.add_floor_failure, Toast.LENGTH_SHORT).show();
        }
    }

    /*    private byte[] readFromURI(Uri uri) throws Exception {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1048576];//buffer size 1024*1024
            int len=0;
            long total=0;
            while ((len=inputStream.read(buffer))>0) {
                total+=len;
                byteArrayOutputStream.write(buffer);
            }
            System.out.println(len);
            return byteArrayOutputStream.toByteArray();
        }
    */
    @Override
    public boolean onLongClick(View view) {
        boolean successful = false;
        try {
            successful = this.localSurveyDatabase.deleteFloor(Integer.parseInt(((TextView) view.findViewById(R.id.floor_id)).getText().toString()), this.floorCatalogueViewModel.block_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (successful) {
            Toast.makeText(this, R.string.delete_floor_successful, Toast.LENGTH_SHORT).show();
            floorCatalogueViewModel.updateCatalogue();
            refreshCatalogueView();
        } else {
            Toast.makeText(this, R.string.delete_floor_failure, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void addFloorElement(FloorElement floorElement) {
        LinearLayout floorElementView = (LinearLayout) this.layoutInflater.inflate(R.layout.floor_element, floorElementsHolder, false);
        String floor_coordinates = floorElement.height + "m," + floorElement.width + "m";
        floorElementView.setOnClickListener(this);
        floorElementView.setOnLongClickListener(this);
        ((TextView) floorElementView.findViewById(R.id.floor_id)).setText("" + floorElement.id);
        ((TextView) floorElementView.findViewById(R.id.floor_coordinates)).setText(floor_coordinates);
        floorElementsHolder.addView(floorElementView);
    }

    private void refreshCatalogueView() {
        floorElementsHolder.removeAllViews();
        for (FloorElement floorElement : this.floorCatalogueViewModel.floorElements) {
            addFloorElement(floorElement);
        }
    }
}
