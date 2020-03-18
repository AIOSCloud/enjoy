package e.word.net.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/*******************************************************************************
 * 版权信息：北京中通天鸿武汉分公司
 * @author xuchang
 * Copyright: Copyright (c) 2007北京中通天鸿武汉分公司,Inc.All Rights Reserved.
 * Description: MapReduce使用oss的工具类
 ******************************************************************************/
public class MyHttpClient {
    private static final Logger logger = Logger.getLogger(MyHttpClient.class);

    public static String post(String url, String json) {
        HttpClient httpclient = new DefaultHttpClient();
        String content = null;
        try {
            HttpPost httppost = new HttpPost(url);
            StringEntity postEntity = new StringEntity(json);
            postEntity.setContentEncoding("UTF-8");
            //发送json数据需要设置contentType
            postEntity.setContentType("application/json");
            httppost.setEntity(postEntity);
            // 执行
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            //返回json格式数据
            content = EntityUtils.toString(resEntity);
            if (StringUtils.isEmpty(content)) {
                logger.error("[httpUtils] 返回的结果类型不包含结果  返回的结果为空");
            }
            logger.debug("[httpUtils] load TaskInfo success");
            // 关闭连接,释放资源
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("access api url wrong!!" + url);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return content;
    }

    public static String get(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        String content = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("access api url wrong!!");
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return content;
    }
}
