package lin.xposed.common.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONUtil {

    /*
     * 循环解析JSON时并做判断键值对
     */
    public static void analysisJson(Object objJson, Action action) {
        //如果objJson为json数组
        if (objJson instanceof JSONArray objArray) {
            for (int i = 0; i < objArray.length(); i++) {
                try {
                    analysisJson(objArray.get(i), action);
                } catch (JSONException ignored) {

                }
            }
        } else if (objJson instanceof JSONObject jsonObject) { //如果objJson为json对象
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                try {
                    String key = it.next();
                    Object value = jsonObject.get(key); //value
                    if (value instanceof JSONArray objArray) { //如果value是数组
                        analysisJson(objArray, action);
                    } else if (value instanceof JSONObject) { //如果value是json对象
                        analysisJson(value, action);
                    } else { //如果value是基本类型
                        action.operate(key, value);
                    }
                } catch (JSONException ignored) {

                }
            }
        } else { //objJson为基本类型
            action.operate(null, objJson);
        }
    }

    public interface Action {
        <T> void operate(T key, T value);
    }

}
