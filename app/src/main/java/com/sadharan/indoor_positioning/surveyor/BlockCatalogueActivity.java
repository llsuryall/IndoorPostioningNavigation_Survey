package com.sadharan.indoor_positioning.surveyor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BlockCatalogueActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener, View.OnLongClickListener {
    private LocalSurveyDatabase localSurveyDatabase;
    private AlertDialog.Builder addBlockAlertBoxBuilder;
    private LayoutInflater layoutInflater;
    private LinearLayout blockElementsHolder;
    private long building_id;
    private EditText blockNameInput, blockLatitudeInput, blockLongitudeInput;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_catalogue_activity);
        initialize();
        updateCatalogue();
    }

    private void initialize() {
        this.localSurveyDatabase = new LocalSurveyDatabase(getApplicationContext());
        this.addBlockAlertBoxBuilder = new AlertDialog.Builder(BlockCatalogueActivity.this);
        this.layoutInflater = LayoutInflater.from(BlockCatalogueActivity.this);
        View addBlockContent = this.layoutInflater.inflate(R.layout.add_block_dialog, null);
        this.addBlockAlertBoxBuilder.setTitle(R.string.add_block)
                .setPositiveButton(R.string.save_button, this)
                .setNegativeButton(R.string.cancel_button, null)
                .setView(addBlockContent)
                .setCancelable(false);
        this.blockElementsHolder = findViewById(R.id.block_elements_holder);
        this.building_id = getIntent().getLongExtra(getString(R.string.building_id_field), -1);
        getIntent().removeExtra(getString(R.string.building_id_field));
        findViewById(R.id.addBlockButton).setOnClickListener(this);
        blockNameInput = addBlockContent.findViewById(R.id.block_name_input);
        blockLatitudeInput = addBlockContent.findViewById(R.id.block_latitude_input);
        blockLongitudeInput = addBlockContent.findViewById(R.id.block_longitude_input);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addBlockButton) {
            AlertDialog addBlockAlertBox = this.addBlockAlertBoxBuilder.create();
            addBlockAlertBox.show();
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String block_name = blockNameInput.getText().toString();
        float block_latitude, block_longitude;
        try {
            block_latitude = Float.parseFloat(blockLatitudeInput.getText().toString());
            block_longitude = Float.parseFloat(blockLongitudeInput.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.add_block_failure, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean successful;
        try {
            successful = localSurveyDatabase.addBlock(new BlockElement(-1, building_id, block_name, block_latitude, block_longitude));
        } catch (Exception e) {
            successful = false;
        }
        if (successful) {
            Toast.makeText(getApplicationContext(), R.string.add_block_successful, Toast.LENGTH_SHORT).show();
            updateCatalogue();
            blockNameInput.setText("");
            blockLatitudeInput.setText("");
            blockLongitudeInput.setText("");
        } else {
            Toast.makeText(getApplicationContext(), R.string.add_block_failure, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (this.localSurveyDatabase.deleteBlock(Long.parseLong(((TextView) view.findViewById(R.id.block_id)).getText().toString()))) {
            Toast.makeText(this, R.string.delete_block_successful, Toast.LENGTH_SHORT).show();
            updateCatalogue();
        }
        return true;
    }

    private void updateCatalogue() {
        ArrayList<BlockElement> blockElements = localSurveyDatabase.getBlockList(this.building_id);
        blockElementsHolder.removeAllViews();
        for (BlockElement blockElement : blockElements) {
            addBlockElement(blockElement);
        }
    }

    private void addBlockElement(BlockElement blockElement) {
        LinearLayout blockElementView = (LinearLayout) this.layoutInflater.inflate(R.layout.block_element, blockElementsHolder, false);
        String block_id_string = Long.toString(blockElement.id);
        String block_coordinates = blockElement.latitude + "," + blockElement.longitude;
        blockElementView.setOnLongClickListener(this);
        ((TextView) blockElementView.findViewById(R.id.block_id)).setText(block_id_string);
        ((TextView) blockElementView.findViewById(R.id.block_name)).setText(blockElement.name);
        ((TextView) blockElementView.findViewById(R.id.block_coordinates)).setText(block_coordinates);
        blockElementsHolder.addView(blockElementView);
    }
}
