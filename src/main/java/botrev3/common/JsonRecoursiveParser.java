package botrev3.common;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Collection;
import java.util.Set;

public class JsonRecoursiveParser {
    private JsonRecoursiveParser(){}
    private static JsonRecoursiveParser recParser;
    private JSONParser parser = new JSONParser();

    public static JsonRecoursiveParser getParser(){
        return recParser==null?new JsonRecoursiveParser():recParser;
    }

    public String jsonFindByKey(String key, InputStream is){
        String result = null;
        JSONObject jsonObj;
        try {
            jsonObj = (JSONObject) parser.parse(new InputStreamReader(is));
            if (!jsonObj.isEmpty()){
                result= jsonRecoursiveFind(jsonObj , key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String safeJsonFindByKey(String key, InputStream is) throws ParseException {
        String result = null;
        JSONObject jsonObj;
        BufferedReader streamReader;
        StringBuilder responseStrBuilder = new StringBuilder();
        try {
            streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int a = responseStrBuilder.indexOf("[")+1;
        int b = responseStrBuilder.lastIndexOf("]");
        String jsonSource = responseStrBuilder.substring(a,b);
        jsonObj = (JSONObject) parser.parse(jsonSource);
        if (jsonObj!=null&&!jsonObj.isEmpty()){
                result= jsonRecoursiveFind(jsonObj , key);
            }
        return result;
    }

    private String jsonRecoursiveFind(JSONObject jsonObject, String key){
        String result = null;
        Set<Object> keys = jsonObject.keySet();
        if (!keys.contains(key)){
            for (Object jsonKey: keys){
                Object value = jsonObject.get(jsonKey);
                if (jsonObject.getClass().isInstance(value)){
                    result = jsonRecoursiveFind((JSONObject) value, key);
                    if (result!=null) break;
                }
                if (JSONArray.class.isInstance(value)){
                    result = jsonArrayChecker((JSONArray) value, key);
                    if (result!=null) break;
                }
            }
        }else result =  jsonObject.get(key).toString();
        return result;
    }

    private String jsonArrayChecker(JSONArray arr, String key){
        String result = null;
        for (Object obj:arr){
            if (JSONObject.class.isInstance(obj)){
                result = jsonRecoursiveFind((JSONObject) obj, key);
                if (result!=null) break;
            }
            if (JSONArray.class.isInstance(obj)){
                result = jsonArrayChecker((JSONArray) obj, key);
                if (result!=null) break;
            }
        }
        return result;
    }


//    public JSONObject jsonFindByValue(String value, InputStream is){
//        String [] matches = value.split(" ");
//        JSONObject result = null;
//        JSONObject jsonObj;
//        try {
//            jsonObj = (JSONObject) parser.parse(new InputStreamReader(is));
//            if (!jsonObj.isEmpty()){
//                if (matches.length>1){
//                    result = jsonFindLastElemOfSentence(jsonObj, matches);
//                }else result= jsonRecoursiveFindByValue(jsonObj , value);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    private JSONObject jsonRecoursiveFindByValue(JSONObject jsonObject, String value){
        JSONObject result = null;
        Collection<Object> values = jsonObject.values();
        Set<Object> keys = jsonObject.keySet();
        if (!values.contains(value)){
            for (Object key: keys){
                Object jsonValue = jsonObject.get(key);
                if (jsonObject.getClass().isInstance(jsonValue)){
                    result = jsonRecoursiveFindByValue((JSONObject) jsonValue, value);
                    if (result!=null) break;
                }
                if (JSONArray.class.isInstance(jsonValue)){
                    result = jsonArrayCheckerByValue((JSONArray) jsonValue, value);
                    if (result!=null) break;
                }
            }
        }else result = jsonObject;
        return result;
    }

    private JSONObject jsonArrayCheckerByValue(JSONArray arr, String value){
        JSONObject result = null;
        for (Object obj:arr){
            if (JSONObject.class.isInstance(obj)){
                result = jsonRecoursiveFindByValue((JSONObject) obj, value);
                if (result!=null) break;
            }
            if (JSONArray.class.isInstance(obj)){
                result = jsonArrayCheckerByValue((JSONArray) obj, value);
                if (result!=null) break;
            }
        }
        return result;
    }


//    public JSONObject jsonFindLastElemOfSentence(JSONObject jsonObject, String[]matches){
//        JSONObject obj = null;
//        // todo разхардкодить
//        List<ParsedWord> results = jsonFindAllByKey(jsonObject, "WordText");
//        for (int i = 0; i<(results.size()-matches.length);i++){
//            int count=0;
//            for (int j = 0; j<matches.length;j++){
//                if (results.get(i+j).getWord().equals(matches[j])){
//                    count++;
//                }
//            }
//            if (count==matches.length){
//                obj =  results.get(i+matches.length-1).getObj();
//            }
//        }return obj;
//    }
//
//
//    public List<ParsedWord> jsonFindAllByKey(JSONObject jsonObject, String key){
//        List<ParsedWord> results = new ArrayList<>();
//        jsonRecoursiveFindAll(jsonObject , results, key);
//        return results;
//    }
//
//    private void jsonRecoursiveFindAll(JSONObject jsonObject, List<ParsedWord> results, String key){
//        Set<Object> keys = jsonObject.keySet();
//        if (!keys.contains(key)){
//            for (Object jsonKey: keys){
//                Object value = jsonObject.get(jsonKey);
//                if (jsonObject.getClass().isInstance(value)){
//                    jsonRecoursiveFindAll((JSONObject) value,results, key);
//                }
//                if (JSONArray.class.isInstance(value)){
//                    jsonArrayCheckerAll((JSONArray) value,results, key);
//                }
//            }
//        }else results.add(new ParsedWord(jsonObject,jsonObject.get(key).toString()));
//    }
//
//    private void jsonArrayCheckerAll(JSONArray arr, List<ParsedWord> results, String key){
//        for (Object obj:arr){
//            if (JSONObject.class.isInstance(obj)){
//                jsonRecoursiveFindAll((JSONObject) obj, results, key);
//            }
//            if (JSONArray.class.isInstance(obj)){
//                jsonArrayCheckerAll((JSONArray) obj,results, key);
//            }
//        }
//    }
}
