package com.example.jiangliu.weather_report.Class;

import android.content.Context;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * Created by jiangliu on 15-1-7.
 */
public class WebAccessTools {
    /**
     * 当前的Context上下文对象
     */
    private Context context;
    /**
     * 构造一个网站访问工具类
     * @param context 记录当前Activity中的Context上下文对象
     */
    public WebAccessTools(Context context) {
        this.context = context;
    }

    /**
     * 根据给定的url地址访问网络，得到响应内容(这里为GET方式访问)
     * @param url 指定的url地址
     * @return web服务器响应的内容，为<code>String</code>类型，当访问失败时，返回为null
     */
    public  String getWebContent(String url) {
        //创建一个http请求对象
        HttpGet request = new HttpGet(url);
        //create HttpClient
        HttpClient httpClient = CustomHttpClient.getHttpClient();
        try{
            //执行请求参数项
            HttpResponse response = httpClient.execute(request);
            //判断是否请求成功
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //获得响应信息
                String content = EntityUtils.toString(response.getEntity());
                return content;
            } else {
                //网连接失败，使用Toast显示提示信息
                Toast.makeText(context, "网络访问失败，请检查您机器的联网设备!", Toast.LENGTH_LONG).show();
            }

        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            //释放网络连接资源
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }
}

