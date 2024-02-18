package com.meteor.setu.util;

import com.alibaba.fastjson2.JSON;
import com.meteor.setu.Options;
import com.meteor.setu.PluginMain;
import com.meteor.setu.model.DataItem;
import com.meteor.setu.model.GitHubRelease;
import com.meteor.setu.model.SetuResponse;
import com.meteor.setu.model.Urls;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.List;
import java.util.Set;

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


    @Data
    @AllArgsConstructor
    public static class Setu{
        private File file;
        private DataItem dataItem;
    }

    public static Setu getSetu(String tag){


        String url = "https://api.lolicon.app/setu/v2?size=original&size=regular&r18=[r18]&excludeAI=[ai]";

        url = url.replace("[r18]", Options.optionsMap.getOrDefault("r18","0"))
                .replace("[ai]",Options.optionsMap.getOrDefault("ai","false"));

        if(tag!=null){
            url += ("&"+tag);
        }

        Request request = new Request.Builder()
                .url(url)
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
                return new Setu(downloadSetu(regular),dataItem);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public static GitHubRelease getLatestRelease(String owner, String repo) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 使用Fastjson解析响应体为GitHubRelease对象
            return JSON.parseObject(response.body().string(), GitHubRelease.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
