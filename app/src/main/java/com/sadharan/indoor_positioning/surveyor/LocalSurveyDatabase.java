package com.sadharan.indoor_positioning.surveyor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocalSurveyDatabase extends SQLiteOpenHelper {
    Context applicationContext;
    //Database
    private static final String DB_NAME = "LocalSurveyDatabase";
    private static final int DB_VERSION = 1;
    //Constants
    private static final String not_null_constraint = "NOT NULL",
            auto_increment_constraint = "AUTOINCREMENT",
            primary_key_constraint = "PRIMARY KEY";
    //Tables
    //Building Table
    private static final String
            TABLE_BUILDING = "Buildings",
            COL_BUILDING_ID = "BuildingID",
            DATA_TYPE_BUILDING_ID = "INTEGER",
            CONSTRAINT_BUILDING_ID = primary_key_constraint + " " + auto_increment_constraint + " " + not_null_constraint,
            COL_BUILDING_NAME = "BuildingName",
            DATA_TYPE_BUILDING_NAME = "varchar(25)",
            CONSTRAINT_BUILDING_NAME = not_null_constraint,
            COL_BUILDING_ADDRESS = "BuildingAddress",
            DATA_TYPE_BUILDING_ADDRESS = "varchar(100)",
            CONSTRAINT_BUILDING_ADDRESS = not_null_constraint;
    private static final String OTHER_CONSTRAINTS_BUILDING_TABLE = "";
    //Block Table
    private static final String
            TABLE_BLOCK = "Blocks",
            COL_BLOCK_ID = "BlockID",
            DATA_TYPE_BLOCK_ID = "INTEGER",
            CONSTRAINT_BLOCK_ID = primary_key_constraint + " " + auto_increment_constraint + " " + not_null_constraint,
            COL_BLOCK_NAME = "BlockName",
            DATA_TYPE_BLOCK_NAME = "varchar(25)",
            CONSTRAINT_BLOCK_NAME = not_null_constraint,
            COL_BLOCK_LATITUDE = "BlockLatitude",
            DATA_TYPE_BLOCK_LATITUDE = "REAL",
            CONSTRAINT_BLOCK_LATITUDE = not_null_constraint,
            COL_BLOCK_LONGITUDE = "BlockLongitude",
            DATA_TYPE_BLOCK_LONGITUDE = "REAL",
            CONSTRAINT_BLOCK_LONGITUDE = not_null_constraint;
    private static final String OTHER_CONSTRAINTS_BLOCK_TABLE = ",FOREIGN KEY(" + COL_BUILDING_ID + ") references " + TABLE_BUILDING + "(" + COL_BUILDING_ID + ") ON DELETE CASCADE ON UPDATE CASCADE";

    public LocalSurveyDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.applicationContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s(%s %s %s,%s %s %s,%s %s %s %s)",
                TABLE_BUILDING,
                COL_BUILDING_ID, DATA_TYPE_BUILDING_ID, CONSTRAINT_BUILDING_ID,
                COL_BUILDING_NAME, DATA_TYPE_BUILDING_NAME, CONSTRAINT_BUILDING_NAME,
                COL_BUILDING_ADDRESS, DATA_TYPE_BUILDING_ADDRESS, CONSTRAINT_BUILDING_ADDRESS,
                OTHER_CONSTRAINTS_BUILDING_TABLE)
        );
        db.execSQL(String.format("CREATE TABLE %s(%s %s %s,%s %s %s,%s %s %s,%s %s %s,%s %s %s %s)",
                TABLE_BLOCK,
                COL_BLOCK_ID, DATA_TYPE_BLOCK_ID, CONSTRAINT_BLOCK_ID,
                COL_BUILDING_ID, DATA_TYPE_BUILDING_ID, not_null_constraint,
                COL_BLOCK_NAME, DATA_TYPE_BLOCK_NAME, CONSTRAINT_BLOCK_NAME,
                COL_BLOCK_LATITUDE, DATA_TYPE_BLOCK_LATITUDE, CONSTRAINT_BLOCK_LATITUDE,
                COL_BLOCK_LONGITUDE, DATA_TYPE_BLOCK_LONGITUDE, CONSTRAINT_BLOCK_LONGITUDE,
                OTHER_CONSTRAINTS_BLOCK_TABLE)
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s %s;", TABLE_BUILDING, TABLE_BLOCK));
        onCreate(db);
    }

    public boolean addBuilding(BuildingElement buildingElement) {
        if (buildingElement.name.length() <= 0) return false;
        if (buildingElement.address.length() <= 0) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues building_row = new ContentValues();
        building_row.put(COL_BUILDING_NAME, buildingElement.name);
        building_row.put(COL_BUILDING_ADDRESS, buildingElement.address);
        long row_id = db.insert(TABLE_BUILDING, null, building_row);
        return row_id >= 0;
    }

    public boolean deleteBuilding(long building_id) {
        String selection = COL_BUILDING_ID + " = ?";
        String[] selectionArgs = {"" + building_id};
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BUILDING, selection, selectionArgs) > 0;
    }

    public ArrayList<BuildingElement> getBuildingList() {
        String[] required_columns = {COL_BUILDING_ID, COL_BUILDING_NAME, COL_BUILDING_ADDRESS};
        Cursor current_building_row = this.getReadableDatabase().query(TABLE_BUILDING, required_columns, null, null, null, null, null);
        ArrayList<BuildingElement> buildingElements = new ArrayList<>();
        while (current_building_row.moveToNext()) {
            try {
                buildingElements.add(new BuildingElement(
                                current_building_row.getLong(current_building_row.getColumnIndexOrThrow(COL_BUILDING_ID)),
                                current_building_row.getString(current_building_row.getColumnIndexOrThrow(COL_BUILDING_NAME)),
                                current_building_row.getString(current_building_row.getColumnIndexOrThrow(COL_BUILDING_ADDRESS))
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        current_building_row.close();
        return buildingElements;
    }

    public boolean addBlock(BlockElement blockElement) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues building_row = new ContentValues();
        building_row.put(COL_BUILDING_ID, blockElement.building_id);
        building_row.put(COL_BLOCK_NAME, blockElement.name);
        building_row.put(COL_BLOCK_LATITUDE, blockElement.latitude);
        building_row.put(COL_BLOCK_LONGITUDE, blockElement.longitude);
        long row_id = db.insert(TABLE_BLOCK, null, building_row);
        return row_id >= 0;
    }

    public boolean deleteBlock(long block_id) {
        String selection = COL_BLOCK_ID + " = ?";
        String[] selectionArgs = {"" + block_id};
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BLOCK, selection, selectionArgs) > 0;
    }

    public ArrayList<BlockElement> getBlockList(long building_id) {
        String[] required_columns = {COL_BLOCK_ID, COL_BUILDING_ID, COL_BLOCK_NAME, COL_BLOCK_LATITUDE, COL_BLOCK_LONGITUDE};
        String selection_criteria = COL_BUILDING_ID + " = ?";
        String[] selection_criteria_arguments = {Long.toString(building_id)};
        Cursor current_block_row = this.getReadableDatabase().query(TABLE_BLOCK, required_columns, selection_criteria, selection_criteria_arguments, null, null, null);
        ArrayList<BlockElement> blockElements = new ArrayList<>();
        while (current_block_row.moveToNext()) {
            try {
                blockElements.add(new BlockElement(
                                current_block_row.getLong(current_block_row.getColumnIndexOrThrow(COL_BLOCK_ID)),
                                current_block_row.getLong(current_block_row.getColumnIndexOrThrow(COL_BUILDING_ID)),
                                current_block_row.getString(current_block_row.getColumnIndexOrThrow(COL_BLOCK_NAME)),
                                current_block_row.getFloat(current_block_row.getColumnIndexOrThrow(COL_BLOCK_LATITUDE)),
                                current_block_row.getFloat(current_block_row.getColumnIndexOrThrow(COL_BLOCK_LONGITUDE))
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        current_block_row.close();
        return blockElements;
    }
}
