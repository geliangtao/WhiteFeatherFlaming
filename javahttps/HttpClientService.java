package com.ss.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpClient发送GET、POST请求
 * @Author libin
 * @CreateDate 2018.5.28 16:56
 */
public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);
    /**
     * 返回成功状态码
     */
    private static final int SUCCESS_CODE = 200;

    public static void main(String[] arg) throws Exception {
        String url = "https://127.0.0.1:443";
        /**
         * 参数值
         */
        Object [] params = new Object[]{"param1","param2"};
        /**
         * 参数名
         */
        Object [] values = new Object[]{"value1","value2"};
        /**
         * 获取参数对象
         */
        List<NameValuePair> paramsList = HttpClientService.getParams(params, values);
        /**
         * 发送get
         */
        Object result = HttpClientService.sendGet(url, paramsList);
        /**
         * 发送post
         */
//        Object result2 = HttpClientService.sendPost(url, paramsList);

        System.out.println("GET返回信息：" + result);
//        System.out.println("POST返回信息：" + result2);
    }

    /**
     * 发送GET请求
     * @param url   请求url
     * @param nameValuePairList    请求参数
     * @return JSON或者字符串
     * @throws Exception
     */
    public static Object sendGet(String url, List<NameValuePair> nameValuePairList) throws Exception{
        JSONObject jsonObject = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            /**
             * 创建HttpClient对象
             */
//            client = HttpClients.createDefault();
            client = new SSLClient();
            /**
             * 创建URIBuilder
             */
            URIBuilder uriBuilder = new URIBuilder(url);
            /**
             * 设置参数
             */
            uriBuilder.addParameters(nameValuePairList);
            /**
             * 创建HttpGet
             */
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            /**
             * 设置请求头部编码
             */
            httpGet.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            /**
             * 设置返回编码
             */
            httpGet.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
            /**
             * 请求服务
             */
            response = client.execute(httpGet);
            /**
             * 获取响应吗
             */
            int statusCode = response.getStatusLine().getStatusCode();

            if (SUCCESS_CODE == statusCode){
                /**
                 * 获取返回对象
                 */
                HttpEntity entity = response.getEntity();
                /**
                 * 通过EntityUitls获取返回内容
                 */
                String result = EntityUtils.toString(entity,"UTF-8");
                /**
                 * 转换成json,根据合法性返回json或者字符串
                 */
                try{
                    jsonObject = JSONObject.parseObject(result);
                    return jsonObject;
                }catch (Exception e){
                    return result;
                }
            }else{
                LOGGER.error("HttpClientService-line: {}, errorMsg{}", 97, "GET请求失败！");
            }
        }catch (Exception e){
            LOGGER.error("HttpClientService-line: {}, Exception: {}", 100, e);
        } finally {
            response.close();
            client.close();
        }
        return null;
    }

    /**
     * 发送POST请求
     * @param url
     * @param nameValuePairList
     * @return JSON或者字符串
     * @throws Exception
     */
    public static Object sendPost(String url, List<NameValuePair> nameValuePairList) throws Exception{
        JSONObject jsonObject = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            /**
             *  创建一个httpclient对象
             */
//            client = HttpClients.createDefault();
            client = new SSLClient();
            /**
             * 创建一个post对象
             */
            HttpPost post = new HttpPost(url);
            /**
             * 包装成一个Entity对象
             */
            StringEntity entity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
            /**
             * 设置请求的内容
             */
            post.setEntity(entity);
            /**
             * 设置请求的报文头部的编码
             */
            post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            /**
             * 设置请求的报文头部的编码
             */
            post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
            /**
             * 执行post请求
             */
            response = client.execute(post);
            /**
             * 获取响应码
             */
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode){
                /**
                 * 通过EntityUitls获取返回内容
                 */
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                /**
                 * 转换成json,根据合法性返回json或者字符串
                 */
                try{
                    jsonObject = JSONObject.parseObject(result);
                    return jsonObject;
                }catch (Exception e){
                    return result;
                }
            }else{
                LOGGER.error("HttpClientService-line: {}, errorMsg：{}", 146, "POST请求失败！");
            }
        }catch (Exception e){
            LOGGER.error("HttpClientService-line: {}, Exception：{}", 149, e);
        }finally {
            response.close();
            client.close();
        }
        return null;
    }

    /**
     * 组织请求参数{参数名和参数值下标保持一致}
     * @param params    参数名数组
     * @param values    参数值数组
     * @return 参数对象
     */
    public static List<NameValuePair> getParams(Object[] params, Object[] values){
        /**
         * 校验参数合法性
         */
        boolean flag = params.length>0 && values.length>0 &&  params.length == values.length;
        if (flag){
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            for(int i =0; i<params.length; i++){
                nameValuePairList.add(new BasicNameValuePair(params[i].toString(),values[i].toString()));
            }
            return nameValuePairList;
        }else{
            LOGGER.error("HttpClientService-line: {}, errorMsg：{}", 197, "请求参数为空且参数长度不一致");
        }
        return null;
    }
}
