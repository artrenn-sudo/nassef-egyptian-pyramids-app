package com.egyptianpyramids;

import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class JSONFile {

  // read a json file and return an array
  public static JSONArray readArray(String fileName) {
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    JSONArray data = null;

    try (InputStream is = JSONFile.class.getResourceAsStream(fileName)) {
      if (is == null) {
        throw new FileNotFoundException("Resource not found: " + fileName);
      }
      try (InputStreamReader reader = new InputStreamReader(is)) {
        Object obj = jsonParser.parse(reader);
        data = (JSONArray) obj;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return data;
  }
}
