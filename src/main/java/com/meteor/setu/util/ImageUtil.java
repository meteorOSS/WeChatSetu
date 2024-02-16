package com.meteor.setu.util;

import com.alibaba.fastjson2.JSON;
import com.meteor.setu.PluginMain;
import com.meteor.setu.model.DataItem;
import com.meteor.setu.model.SetuResponse;
import com.meteor.setu.model.Urls;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.List;

public class ImageUtil {

    private static OkHttpClient okHttpClient = new OkHttpClient();


    public static File downloadSetu(String url){
        Request request = new Request.Builder().url(url).build();

        try (Response response = okHttpClient.newCall(request).execute()) {

            // 从URL中提取文件名
            String filename = url.substring(url.lastIndexOf('/') + 1);
            File file = new File(System.getProperty("user.dir"), filename);

            // 读取响应体并写入文件
            try (InputStream in = response.body().byteStream();
                 OutputStream out = new FileOutputStream(file)) {
                byte[] buf = new byte[2048];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }

            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getSetu(){

        Request request = new Request.Builder()
                .url("https://api.lolicon.app/setu/v2?size=original&size=regular")
                .get()
                .build();

        try(            Response response = okHttpClient.newCall(request).execute();
        ) {
            SetuResponse setuResponse = JSON.toJavaObject(response.body().string(), SetuResponse.class);
            List<DataItem> data = setuResponse.getData();
            if(data!=null&&!data.isEmpty()){
                DataItem dataItem = data.get(0);
                Urls urls = dataItem.getUrls();
                String regular = urls.getRegular();
                return downloadSetu(regular);
            }
        } catch (IOException e) {
        }
        return null;
    }

}
