package org.dai.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
    private static PoolingHttpClientConnectionManager cm;
    private static RequestConfig requestConfig;
    private static CloseableHttpClient httpClient; //
    private static int  MAX_TIME_OUT = 60000;
    private static int SOCKET_TIME_OUT = 90000; // 等待响应超时（读取数据超时）socketTimeout
    private static CookieStore cookieStore;
    private static String ContentType_Application_Json ="application/json";
    private static String ContentType_Text_Json ="text/json";
    private static ResponseHandler<CloseableHttpResponse> responseHandler; //响应处理器，内部自动关闭流

    private static class HttpUtilsHolder{
        private static final HttpUtils INSTANCE = new HttpUtils();
    }

    // 构造方法
    private HttpUtils(){
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(2);

        requestConfig = RequestConfig.custom()
                .setConnectTimeout(MAX_TIME_OUT)
                .setSocketTimeout(SOCKET_TIME_OUT)
                .setConnectionRequestTimeout(MAX_TIME_OUT)
                .setCookieSpec(CookieSpecs.DEFAULT)
                .build();

        cookieStore = new BasicCookieStore();
        responseHandler = new MyResponseHandler();
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    public String getCookie(String key){
        List<Cookie> cookies = cookieStore.getCookies();
        String cookie = null;
        for (Cookie coo:cookies){
            if (coo.getName().equals(key)){
                cookie = coo.getName()+ "---" + coo.getValue();
            }
        }
        return cookie;
    }

    /**
     * get请求，入参为 map
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public String get(String url,Map params) throws Exception{
        StringBuffer paramsStr = new StringBuffer();
        if (params != null && !params.isEmpty()){
            Set<String> paramsKeySet = params.keySet();
            for (String key: paramsKeySet){
                paramsStr=paramsStr.append(key).append("=").append(params.get(key));
            }
        }
        String paramsS = paramsStr.toString();
        String res = get(url,paramsS);
        return res;
    }

    /**
     *  get请求，入参为 String
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public String get(String url,String params) throws Exception{
        StringBuffer apiUrl = new StringBuffer(url);
        if (params != null &&  !params.isEmpty()){
            apiUrl = apiUrl.append("?").append(params);
        }
        HttpGet httpGet = new HttpGet(apiUrl.toString());
        CloseableHttpResponse response = null;
        String res = null;
        try{
            response = httpClient.execute(httpGet);
            res = EntityUtils.toString(response.getEntity());
            return  res;
        }catch (Exception e){
            // 在catch中抛出异常，不会影响finally的执行。JVM会先执行finally，然后抛出异常。
            // finally抛出异常后，原来在catch中准备抛出的异常就“消失”了，因为只能抛出一个异常。
            e.printStackTrace();
            throw e;
        }finally {
            // 如果没有发生异常，就正常执行try { ... }语句块，然后执行finally。
            // 如果发生了异常，就中断执行try { ... }语句块，然后跳转执行匹配的catch语句块，最后执行finally。
            // 绝大多数情况下，在finally中不要抛出异常。
                response.close();
        }
    }

    // 自定义header的 get请求
    public String getWithHeader(String url,String params,String header) throws Exception{

        return "";
    }

    /**
     * 模拟 content-type为application/json数据格式的请求
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public String postJson(String url,String json) throws Exception{

        HttpPost httpPost = new HttpPost(url);
        String res = null;
        CloseableHttpResponse response =null;
        if (json != null && !json.isEmpty()){
            StringEntity entity = new StringEntity(json,"UTF-8"); // 解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType(ContentType_Application_Json);
            httpPost.setEntity(entity);
        }
        try{
            response = httpClient.execute(httpPost);
            res = EntityUtils.toString(response.getEntity());
            return  res;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            response.close();
        }
    }

    /**
     * 模拟 content-type为 application/x-www-form-urlencoded 的 post请求
     */
    public String post(String url, ArrayList<BasicNameValuePair> params) throws Exception {
        // 使用URL实体转换工具，把输入数据编码成合适的内容（构造一个form表单式的实体）
        // 例如：两个键值对，被UrlEncodedFormEntity实例编码后变为如下内容：
        // param1=value1&param2=value2
        UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params);
        HttpPost httpPost = new HttpPost(url);
        String res = null;
        CloseableHttpResponse response = null;
        if (params != null && !params.isEmpty()){
            httpPost.setEntity(paramsEntity); // 将请求体设置到httpPost对象中
        }
        // 执行post请求
        try{
            response = httpClient.execute(httpPost,responseHandler);
            // 此处使用了响应处理器，所以后面不用再手动显式的关闭连接
            res = EntityUtils.toString(response.getEntity()); // 取返回结果的消息体并转换为String类型返回
            return res;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }


    public static HttpUtils getInstance(){
        return HttpUtilsHolder.INSTANCE;
    }


}

class MyResponseHandler implements ResponseHandler{
    public CloseableHttpResponse handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
        return null;
    }
}
