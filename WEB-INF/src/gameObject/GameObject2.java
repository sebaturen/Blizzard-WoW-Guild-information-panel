package com.blizzardPanel.gameObject;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;

public abstract class GameObject2 {

    public static final DBConnect dbConnect = new DBConnect();

    // Attribute
    private String tableDB;
    private Class<?> classObject;

    public GameObject2(String tableDB, Class<?> classObject) {
        this.tableDB = tableDB;
        this.classObject = classObject;
    }

    protected Object load(String where, long whereValue) { return load(where, new String[]{whereValue+""}); }
    protected Object load(String where, String whereValue) { return load(where, new String[]{whereValue}); }
    protected Object load(String where, String[] whereValue) {
        try {
            JsonArray dbSelect = dbConnect.select(
                    this.tableDB,
                    new String[]{"*"},
                    where,
                    whereValue,
                    true
            );

            if (dbSelect.size() > 0) {
                JsonObject content = dbSelect.get(0).getAsJsonObject();
                return new Gson().fromJson(content, classObject);
            } else {
                Logs.errorLog(this.getClass(), "DATA NOT FOUND");
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to load element ["+ where +"] -> "+ e);
        }
        return null;
    }
}
