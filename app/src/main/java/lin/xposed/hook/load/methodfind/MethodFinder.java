package lin.xposed.hook.load.methodfind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.hook.load.HookItemLoader;
import top.linl.dexparser.DexFinder;

public class MethodFinder {
    private final Class<?> hookItem;
    private final HashMap<String, Method[]> container = new HashMap<>();
    private final DexFinder dexFinder;

    public MethodFinder(Class<?> hookItem, DexFinder dexFinder) {
        this.hookItem = hookItem;
        this.dexFinder = dexFinder;
    }

    public Method[] findMethodString(String str) throws Exception {
        if (HookItemLoader.isMethodFindPeriod.get()) {
            Method[] methods = dexFinder.findMethodString(str).toArray(new Method[0]);
            container.put(str, methods);
            return methods;
        }
        return container.get(str);
    }

    public JSONObject getResults() {
        /*
         * {
         *   "Class" : {
         *                  "key" : [methodInfo,...]
         *              }
         *   "HookItemName" : {
         *                  "key" : [],
         *                  "key" : []
         *                   }
         * }
         *
         * */
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Method[]> entry : container.entrySet()) {
            try {
                JSONArray methodInfoLIst = new JSONArray();
                for (Method method : entry.getValue()) {
                    methodInfoLIst.put(getMethodInfo(method));
                }
                result.put(entry.getKey(), methodInfoLIst);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public void loadAllMethod(JSONObject methodInfoList) throws Exception {
        Iterator<String> iterator = methodInfoList.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONArray value = methodInfoList.getJSONArray(key);
            Method[] methods = new Method[value.length()];
            for (int i = 0; i < value.length(); i++) {
                JSONObject methodInfo = value.getJSONObject(i);
                Method method = findMethodByJSON(methodInfo);
                methods[i] = method;
            }
            container.put(key, methods);
        }
    }

    private Method findMethodByJSON(JSONObject methodInfo) throws Exception {
        String methodName = methodInfo.getString("MethodName");
        String declareClass = methodInfo.getString("DeclareClass");
        String ReturnType = methodInfo.getString("ReturnType");
        JSONArray methodParams = methodInfo.getJSONArray("Params");
        Class<?>[] params = new Class[methodParams.length()];
        for (int i = 0; i < params.length; i++) {
            params[i] = ClassUtils.getClass(methodParams.getString(i));
        }
        return MethodUtils.findMethod(declareClass, methodName, ClassUtils.getClass(ReturnType), params);
    }

    public JSONObject getMethodInfo(Method method) {
        try {
            method.setAccessible(true);
            JSONObject result = new JSONObject();
            String methodName = method.getName();
            String declareClass = method.getDeclaringClass().getName();
            Class<?>[] methodParams = method.getParameterTypes();
            JSONArray params = new JSONArray();
            for (Class<?> type : methodParams) {
                params.put(type.getName());
            }
            result.put("DeclareClass", declareClass);
            result.put("MethodName", methodName);
            result.put("Params", params);
            result.put("ReturnType", method.getReturnType().getName());
            return result;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
