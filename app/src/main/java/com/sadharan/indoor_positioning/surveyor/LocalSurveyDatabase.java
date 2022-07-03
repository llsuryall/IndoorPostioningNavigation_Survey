package com.sadharan.indoor_positioning.surveyor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class LocalSurveyDatabase extends SQLiteOpenHelper {
    private static final String BASE_URL = "https://indoor-positioning-navigation.herokuapp.com/api/v1.0";
    //Database
    private static final String DB_NAME = "LocalSurveyDatabase";
    private static final int DB_VERSION = 1;
    //Constants
    private static final String not_null_constraint = "NOT NULL",
            primary_key_constraint = "PRIMARY KEY";
    //Tables
    //Building Table
    private static final String
            TABLE_BUILDING = "Buildings",
            COL_BUILDING_ID = "BuildingID",
            DATA_TYPE_BUILDING_ID = "INTEGER",
            CONSTRAINT_BUILDING_ID = primary_key_constraint + " " + not_null_constraint,
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
            CONSTRAINT_BLOCK_ID = primary_key_constraint + " " + not_null_constraint,
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
    //Floor Table
    private static final String
            TABLE_FLOOR = "Floors",
            COL_FLOOR_ID = "FloorID",
            DATA_TYPE_FLOOR_ID = "INTEGER",
            CONSTRAINT_FLOOR_ID = not_null_constraint,
            COL_FLOOR_HEIGHT = "FloorHeight",
            DATA_TYPE_FLOOR_HEIGHT = "REAL",
            CONSTRAINT_FLOOR_HEIGHT = not_null_constraint,
            COL_FLOOR_WIDTH = "FloorWidth",
            DATA_TYPE_FLOOR_WIDTH = "REAL",
            CONSTRAINT_FLOOR_WIDTH = not_null_constraint;
    private static final String OTHER_CONSTRAINTS_FLOOR_TABLE = ",FOREIGN KEY(" + COL_BLOCK_ID + ") references " + TABLE_BLOCK + "(" + COL_BLOCK_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
            + ", PRIMARY KEY(" + COL_FLOOR_ID + ", " + COL_BLOCK_ID + ")";
    //Datapoint Table
    private static final String
            //TABLE_DATAPOINT = "Datapoints",
            COL_X_COORDINATE = "X_Coordinate",
            //DATA_TYPE_X_COORDINATE = "REAL",
            //CONSTRAINT_X_COORDINATE = not_null_constraint,
            COL_Y_COORDINATE = "Y_Coordinate",
            //DATA_TYPE_Y_COORDINATE = "REAL",
            //CONSTRAINT_Y_COORDINATE = not_null_constraint,
            COL_AP_SIGNAL_STRENGTHS = "APSignalStrengthsJSON";
            //DATA_TYPE_AP_SIGNAL_STRENGTHS = "varchar(250)",
            //CONSTRAINT_AP_SIGNAL_STRENGTHS = not_null_constraint;
    //private static final String OTHER_CONSTRAINTS_DATAPOINT_TABLE = ",FOREIGN KEY(" + COL_FLOOR_ID + ") references " + TABLE_FLOOR + "(" + COL_FLOOR_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
    //        + ",FOREIGN KEY(" + COL_BLOCK_ID + ") references " + TABLE_BLOCK + "(" + COL_BLOCK_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
    //        + ", PRIMARY KEY(" + COL_X_COORDINATE + ", " + COL_Y_COORDINATE + ", " + COL_FLOOR_ID + ", " + COL_BLOCK_ID + ")";*/

    public LocalSurveyDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Building
        db.execSQL(String.format("CREATE TABLE %s(%s %s %s,%s %s %s,%s %s %s %s)",
                TABLE_BUILDING,
                COL_BUILDING_ID, DATA_TYPE_BUILDING_ID, CONSTRAINT_BUILDING_ID,
                COL_BUILDING_NAME, DATA_TYPE_BUILDING_NAME, CONSTRAINT_BUILDING_NAME,
                COL_BUILDING_ADDRESS, DATA_TYPE_BUILDING_ADDRESS, CONSTRAINT_BUILDING_ADDRESS,
                OTHER_CONSTRAINTS_BUILDING_TABLE)
        );
        //Block
        db.execSQL(String.format("CREATE TABLE %s(%s %s %s,%s %s %s,%s %s %s,%s %s %s,%s %s %s %s)",
                TABLE_BLOCK,
                COL_BLOCK_ID, DATA_TYPE_BLOCK_ID, CONSTRAINT_BLOCK_ID,
                COL_BUILDING_ID, DATA_TYPE_BUILDING_ID, not_null_constraint,
                COL_BLOCK_NAME, DATA_TYPE_BLOCK_NAME, CONSTRAINT_BLOCK_NAME,
                COL_BLOCK_LATITUDE, DATA_TYPE_BLOCK_LATITUDE, CONSTRAINT_BLOCK_LATITUDE,
                COL_BLOCK_LONGITUDE, DATA_TYPE_BLOCK_LONGITUDE, CONSTRAINT_BLOCK_LONGITUDE,
                OTHER_CONSTRAINTS_BLOCK_TABLE)
        );
        //Floor
        db.execSQL(String.format("CREATE TABLE %s(%s %s %s,%s %s %s,%s %s %s,%s %s %s %s)",
                TABLE_FLOOR,
                COL_FLOOR_ID, DATA_TYPE_FLOOR_ID, CONSTRAINT_FLOOR_ID,
                COL_BLOCK_ID, DATA_TYPE_BLOCK_ID, not_null_constraint,
                COL_FLOOR_HEIGHT, DATA_TYPE_FLOOR_HEIGHT, CONSTRAINT_FLOOR_HEIGHT,
                COL_FLOOR_WIDTH, DATA_TYPE_FLOOR_WIDTH, CONSTRAINT_FLOOR_WIDTH,
                OTHER_CONSTRAINTS_FLOOR_TABLE)
        );
        //Datapoint
        /*db.execSQL(String.format("CREATE TABLE %s(%s %s %s,%s %s %s,%s %s %s,%s %s %s,%s %s %s %s)",
                TABLE_DATAPOINT,
                COL_X_COORDINATE, DATA_TYPE_X_COORDINATE, CONSTRAINT_X_COORDINATE,
                COL_Y_COORDINATE, DATA_TYPE_Y_COORDINATE, CONSTRAINT_Y_COORDINATE,
                COL_FLOOR_ID, DATA_TYPE_FLOOR_ID, CONSTRAINT_FLOOR_ID,
                COL_BLOCK_ID, DATA_TYPE_BLOCK_ID, not_null_constraint,
                COL_AP_SIGNAL_STRENGTHS, DATA_TYPE_AP_SIGNAL_STRENGTHS, CONSTRAINT_AP_SIGNAL_STRENGTHS,
                OTHER_CONSTRAINTS_DATAPOINT_TABLE)
        );*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATAPOINT + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOOR + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCK + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING + ";");
        onCreate(db);
    }

    public boolean resetDatabase() throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] table_names = {TABLE_BUILDING, TABLE_BLOCK, TABLE_FLOOR};// TABLE_DATAPOINT};
        String[] table_query_names = {"buildings", "blocks", "floors"};//, "datapoints"};
        //Deletes all data from table recursively
        for (String table_name : table_names) {
            db.delete(table_name, null, null);
        }
        //Fetch and insert from all tables
        for (int i = 0; i < table_names.length; i++) {
            JSONObject response = sendPost(new JSONObject(), "/" + table_query_names[i] + "/all");
            if (response.getBoolean("successful")) {
                JSONArray items = response.getJSONArray("data");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    ContentValues row = new ContentValues();
                    Iterator<String> columns = item.keys();
                    String column;
                    while (columns.hasNext()) {
                        column = columns.next();
                        row.put(column, item.getString(column));
                    }
                    db.insertWithOnConflict(table_names[i], null, row, SQLiteDatabase.CONFLICT_REPLACE);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean addBuilding(BuildingElement buildingElement) throws JSONException {
        JSONObject building_data = new JSONObject();
        building_data.put(COL_BUILDING_NAME, buildingElement.name);
        building_data.put(COL_BUILDING_ADDRESS, buildingElement.address);
        JSONObject response = sendPost(building_data, "/buildings/add");
        if (response.getBoolean("successful")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues building_row = new ContentValues();
            building_row.put(COL_BUILDING_ID, response.getLong("id"));
            building_row.put(COL_BUILDING_NAME, buildingElement.name);
            building_row.put(COL_BUILDING_ADDRESS, buildingElement.address);
            long row_id = db.insertWithOnConflict(TABLE_BUILDING, null, building_row, SQLiteDatabase.CONFLICT_REPLACE);
            return row_id >= 0;
        } else {
            return false;
        }
    }

    public boolean deleteBuilding(long building_id) throws JSONException {
        JSONObject building_data = new JSONObject();
        building_data.put(COL_BUILDING_ID, building_id);
        if (sendPost(building_data, "/buildings/delete").getBoolean("successful")) {
            String selection = COL_BUILDING_ID + " = ?";
            String[] selectionArgs = {"" + building_id};
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_BUILDING, selection, selectionArgs) > 0;
        } else {
            return false;
        }
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

    public boolean addBlock(BlockElement blockElement) throws JSONException {
        JSONObject block_data = new JSONObject();
        block_data.put(COL_BLOCK_NAME, blockElement.name);
        block_data.put(COL_BLOCK_LATITUDE, blockElement.latitude);
        block_data.put(COL_BLOCK_LONGITUDE, blockElement.longitude);
        block_data.put(COL_BUILDING_ID, blockElement.building_id);
        JSONObject response = sendPost(block_data, "/blocks/add");
        if (response.getBoolean("successful")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues block_row = new ContentValues();
            block_row.put(COL_BLOCK_ID, response.getLong("id"));
            block_row.put(COL_BUILDING_ID, blockElement.building_id);
            block_row.put(COL_BLOCK_NAME, blockElement.name);
            block_row.put(COL_BLOCK_LATITUDE, blockElement.latitude);
            block_row.put(COL_BLOCK_LONGITUDE, blockElement.longitude);
            long row_id = db.insertWithOnConflict(TABLE_BLOCK, null, block_row, SQLiteDatabase.CONFLICT_REPLACE);
            return row_id >= 0;
        } else {
            return false;
        }
    }

    public boolean deleteBlock(long block_id) throws JSONException {
        JSONObject block_data = new JSONObject();
        block_data.put(COL_BLOCK_ID, block_id);
        if (sendPost(block_data, "/blocks/delete").getBoolean("successful")) {
            String selection = COL_BLOCK_ID + " = ?";
            String[] selectionArgs = {"" + block_id};
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_BLOCK, selection, selectionArgs) > 0;
        } else {
            return false;
        }
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

    public boolean addFloor(FloorElement floorElement) throws JSONException {
        JSONObject floor_data = new JSONObject();
        floor_data.put(COL_FLOOR_ID, floorElement.id);
        floor_data.put(COL_BLOCK_ID, floorElement.block_id);
        floor_data.put(COL_FLOOR_HEIGHT, floorElement.height);
        floor_data.put(COL_FLOOR_WIDTH, floorElement.width);
        JSONObject response = sendPost(floor_data, "/floors/add");
        if (response.getBoolean("successful")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues floor_row = new ContentValues();
            floor_row.put(COL_FLOOR_ID, floorElement.id);
            floor_row.put(COL_BLOCK_ID, floorElement.block_id);
            floor_row.put(COL_FLOOR_HEIGHT, floorElement.height);
            floor_row.put(COL_FLOOR_WIDTH, floorElement.width);
            long row_id = db.insertWithOnConflict(TABLE_FLOOR, null, floor_row, SQLiteDatabase.CONFLICT_REPLACE);
            return row_id >= 0;
        } else {
            return false;
        }
    }

    public boolean deleteFloor(int floor_id, long block_id) throws JSONException {
        JSONObject floor_data = new JSONObject();
        floor_data.put(COL_FLOOR_ID, floor_id);
        floor_data.put(COL_BLOCK_ID, block_id);
        JSONObject response = sendPost(floor_data, "/floors/delete");
        if (response.getBoolean("successful")) {
            String selection = COL_FLOOR_ID + " = ? AND " + COL_BLOCK_ID + " = ?";
            String[] selectionArgs = {"" + floor_id, "" + block_id};
            SQLiteDatabase db = this.getWritableDatabase();
            int n = db.delete(TABLE_FLOOR, selection, selectionArgs);
            return n > 0;
        } else {
            return false;
        }
    }

    public ArrayList<FloorElement> getFloorList(long block_id) {
        String[] required_columns = {COL_FLOOR_ID, COL_BLOCK_ID, COL_FLOOR_HEIGHT, COL_FLOOR_WIDTH};
        String selection_criteria = COL_BLOCK_ID + " = ?";
        String[] selection_criteria_arguments = {Long.toString(block_id)};
        Cursor current_floor_row = this.getReadableDatabase().query(TABLE_FLOOR, required_columns, selection_criteria, selection_criteria_arguments, null, null, null);
        ArrayList<FloorElement> floorElements = new ArrayList<>();
        while (current_floor_row.moveToNext()) {
            try {
                floorElements.add(new FloorElement(
                                current_floor_row.getInt(current_floor_row.getColumnIndexOrThrow(COL_FLOOR_ID)),
                                current_floor_row.getLong(current_floor_row.getColumnIndexOrThrow(COL_BLOCK_ID)),
                                current_floor_row.getFloat(current_floor_row.getColumnIndexOrThrow(COL_FLOOR_HEIGHT)),
                                current_floor_row.getFloat(current_floor_row.getColumnIndexOrThrow(COL_FLOOR_WIDTH))
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        current_floor_row.close();
        return floorElements;
    }

    public boolean addDatapoint(SurveyElement surveyElement) throws JSONException {
        JSONObject datapoint_data = new JSONObject();
        datapoint_data.put(COL_X_COORDINATE, surveyElement.x_coordinate);
        datapoint_data.put(COL_Y_COORDINATE, surveyElement.y_coordinate);
        datapoint_data.put(COL_FLOOR_ID, surveyElement.floor_id);
        datapoint_data.put(COL_BLOCK_ID, surveyElement.block_id);
        JSONObject signalStrengthJSON = new JSONObject();
        int count = 0;
        for (APSignalStrength apSignalStrength : surveyElement.apSignalStrengths) {
            signalStrengthJSON.put(apSignalStrength.bssid, apSignalStrength.rssi);
            count++;
            if (count >= 10) break;
        }
        datapoint_data.put(COL_AP_SIGNAL_STRENGTHS, signalStrengthJSON.toString());
        JSONObject response = sendPost(datapoint_data, "/datapoints/add");
        return response.getBoolean("successful");
    }

    private static class RequestThread extends Thread {
        private final JSONObject requestData;
        private final String url;
        public JSONObject response;

        RequestThread(JSONObject requestData, String url) {
            this.requestData = requestData;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                this.sendPost();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendPost() throws JSONException {
            HttpsURLConnection HttpsURLConnection;
            OutputStream requestStream;
            InputStream responseStream;
            try {
                HttpsURLConnection = (HttpsURLConnection) new URL(BASE_URL + url).openConnection();
                byte[] requestDataBytes = requestData.toString().getBytes(StandardCharsets.UTF_8);
                HttpsURLConnection.setRequestMethod("POST");
                HttpsURLConnection.setRequestProperty("Content-Type", "application/json");
                HttpsURLConnection.setRequestProperty("Content-Length", "" + requestDataBytes.length);
                requestStream = new BufferedOutputStream(HttpsURLConnection.getOutputStream());
                requestStream.write(requestDataBytes);
                requestStream.close();
                responseStream = new BufferedInputStream(HttpsURLConnection.getInputStream());
                int len = 1;
                StringBuilder responseStringBuilder = new StringBuilder(500);
                byte[] buf = new byte[1024];
                while (len > 0) {
                    len = responseStream.read(buf);
                    responseStringBuilder.append(new String(buf));
                }
                response = new JSONObject(responseStringBuilder.toString());
                responseStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                response = new JSONObject();
                response.put("Error", e.toString());
            }
        }
    }

    public JSONObject sendPost(JSONObject requestData, String url) {
        RequestThread requestThread = new RequestThread(requestData, url);
        requestThread.start();
        JSONObject response = null;
        try {
            requestThread.join();
            response = requestThread.response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
