package com.robo101.patientmonitoringsystem.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    public HashMap<String, String> parseJSONObject(JSONObject object) {
        HashMap<String, String> dataList = new HashMap<>();
        try {
            String name = object.getString("name");

            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");

            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");

            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<HashMap<String, String>> parserJsonArray(JSONArray jsonArray) {

        List<HashMap<String, String>> dataList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                HashMap<String, String> data = parseJSONObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public  List<HashMap<String, String>> parseResult(JSONObject jsonObject) {
        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parserJsonArray(jsonArray);
    }
}
