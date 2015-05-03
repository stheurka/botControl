package com.mycompany.botcontrol;

import com.google.gson.Gson;

/**
 * Created by rhishi on 30/3/15.
 */
public class jsonManager {
    public String convertToJson(dataObject switchObject)
    {
        Gson gson = new Gson();
        return (gson.toJson(switchObject));
    }

    public dataObject convertFromJson(String inputString)
    {
        Gson gson = new Gson();
        return gson.fromJson(inputString, dataObject.class);
    }
}
