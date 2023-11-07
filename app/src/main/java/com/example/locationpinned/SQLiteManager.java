package com.example.locationpinned;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SQLiteManager extends SQLiteOpenHelper {
    private static  SQLiteManager sqLiteManager;
    private static final String DB_NAME = "LocationPinned";
    private static final int DB_VERSION = 5;
    private static final String TABLE_NAME = "location";
    private static final String ID_COL = "id";
    private static final String ADDRESS_COL = "address";
    private static final String LATITUDE_COL = "latitude";
    private static final String LONGITUDE_COL = "longitude";
    private Context context;

    /**
     * Constructor
     * @param context
     */
    public SQLiteManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public static SQLiteManager instanceOfDatabase(Context context){
        if(sqLiteManager == null) sqLiteManager = new SQLiteManager(context);

        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE "+TABLE_NAME+" ("+
                ID_COL+" INTEGER PRIMARY KEY,"+
                ADDRESS_COL+" TEXT,"+
                LATITUDE_COL+" TEXT,"+
                LONGITUDE_COL+" TEXT"
                +" )";

        db.execSQL(sql);

        // read latitude and longitude from file and add to database upon creation
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.lat_lon_pairs);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            // go through every line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    double latitude = Double.parseDouble(parts[0].trim());
                    double longitude = Double.parseDouble(parts[1].trim());

                    // translate the values to an address
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    // save all the values
                    if (!addresses.isEmpty()) {
                        String address = addresses.get(0).getAddressLine(0);
                        // Insert the data into the database
                        ContentValues values = new ContentValues();
                        values.put(ADDRESS_COL, address);
                        values.put(LATITUDE_COL, String.valueOf(latitude));
                        values.put(LONGITUDE_COL, String.valueOf(longitude));
                        db.insert(TABLE_NAME, null, values);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search locations by the address field
     * @param searchText
     * @return
     */
    public List<LocationData> searchByAddress(String searchText) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LocationData> results = new ArrayList<>();

        // Query database
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ADDRESS_COL + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchText + "%"});

        // Go through all results and append to LIST
        if (cursor != null && cursor.moveToFirst()) {
            do {
                LocationData result = new LocationData();
                result.setId(cursor.getInt(0));
                result.setAddress(cursor.getString(1));
                result.setLatitude(cursor.getString(2));
                result.setLongitude(cursor.getString(3));
                results.add(result);
            } while (cursor.moveToNext());

            cursor.close();
        }


        return results;
    }

    /**
     * Edit the latitude and longitude of a location
     * @param location
     */
    public void editLocation(LocationData location) {
        int id = location.getId();
        String newLatitude = location.getLatitude();
        String newLongitude = location.getLongitude();
        SQLiteDatabase db = this.getWritableDatabase();

        // Update the latitude and longitude values for the specified row
        ContentValues values = new ContentValues();
        values.put(LATITUDE_COL, newLatitude);
        values.put(LONGITUDE_COL, newLongitude);
        db.update(TABLE_NAME, values, ID_COL + "=?", new String[]{String.valueOf(id)});

        // Perform geocoding to obtain the new address
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(newLatitude), Double.parseDouble(newLongitude), 1);

            if (!addresses.isEmpty()) {
                String newAddress = addresses.get(0).getAddressLine(0);
                // Update the address in the database
                ContentValues addressValues = new ContentValues();
                addressValues.put(ADDRESS_COL, newAddress);
                db.update(TABLE_NAME, addressValues, ID_COL + "=?", new String[]{String.valueOf(id)});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Add a new location to the database
     * @param newLatitude
     * @param newLongitude
     */
    public void addLocation(String newLatitude, String newLongitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Perform geocoding to obtain the new address
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(newLatitude), Double.parseDouble(newLongitude), 1);

            if (!addresses.isEmpty()) {
                String newAddress = addresses.get(0).getAddressLine(0);

                // Insert the new location into the database
                ContentValues values = new ContentValues();
                values.put(ADDRESS_COL, newAddress);
                values.put(LATITUDE_COL, newLatitude);
                values.put(LONGITUDE_COL, newLongitude);
                db.insert(TABLE_NAME, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Delete an address from the database
     * @param id
     */
    public void deleteLocation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the WHERE clause to specify the ID of the row to delete
        String whereClause = ID_COL + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        // Perform the deletion operation
        db.delete(TABLE_NAME, whereClause, whereArgs);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion >= newVersion)
            return;

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
