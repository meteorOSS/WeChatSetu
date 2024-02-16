package com.meteor.setu.util;

import com.alibaba.fastjson.JSON;
import com.meteor.setu.PluginMain;
import com.meteor.setu.model.VideoInfo;
import lombok.Cleanup;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

public class VideoDownloader {
    
    private static final OkHttpClient client = new OkHttpClient();

    public static File downloadVideo(String apiUrl) throws Exception {
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        @Cleanup Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Failed to download file: " + response);

        VideoInfo videoInfo = JSON.parseObject(response.body().string(), VideoInfo.class);
        
        String videoUrl = "https:" + videoInfo.getMp4(); // 确保URL是完整的
        Request videoRequest = new Request.Builder()
                .url(videoUrl)
                .build();

        @Cleanup Response videoResponse = client.newCall(videoRequest).execute();
        if (!videoResponse.isSuccessful()) throw new IOException("Failed to download file: " + videoResponse);

        // 保存视频到本地
        File file = new File(PluginMain.INSTANCE.getFile(),"downloaded_video.mp4");
        @Cleanup InputStream is = videoResponse.body().byteStream();
        @Cleanup OutputStream os = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        return file;
    }

    // 示例用法
    public static void main(String[] args) {
        try {
            File downloadedFile = downloadVideo("https://tucdn.wpon.cn/api-girl/index.php?wpon=json");
            System.out.println("Video downloaded: " + downloadedFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
