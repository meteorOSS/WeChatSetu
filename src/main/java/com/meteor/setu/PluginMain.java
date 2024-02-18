package com.meteor.setu;

import com.meteor.setu.model.DataItem;
import com.meteor.setu.model.GitHubRelease;
import com.meteor.setu.util.ImageUtil;
import com.meteor.wechatbc.entitiy.message.Message;
import com.meteor.wechatbc.event.EventHandler;
import com.meteor.wechatbc.impl.event.Listener;
import com.meteor.wechatbc.impl.event.sub.ReceiveMessageEvent;
import com.meteor.wechatbc.impl.model.MsgType;
import com.meteor.wechatbc.impl.plugin.BasePlugin;
import lombok.Getter;

import java.io.File;

import static com.meteor.setu.util.VideoDownloader.downloadVideo;

public class PluginMain extends BasePlugin implements Listener {

    @Getter private File file;

    public static PluginMain INSTANCE = null;
    private File patFile;

    @Override
    public void onLoad() {

    }

    private long lastTime = 0L;

    // 25s可调用一次
    public boolean isPass(){
        long last = (System.currentTimeMillis() - lastTime) / 1000;
        if(last>=25) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onReceiveMessage(ReceiveMessageEvent groupMessageEvent){
        Message message = groupMessageEvent.getMessage();
        if(message.getMsgType()== MsgType.TextMsg){
            String content = message.getContent();
            String fromUserName = message.getFromUserName();
            if(content.startsWith("ldst")){
                content = content.replace("？","?")
                        .replace("＝","=");
                if(!isPass()) {
                    getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"寻美需沉鱼落雁之时，频繁之求，恐怕难以觅得佳人。请主公暂且静候");
                    return;
                }
                getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"卷起千堆雪，臣将行遍天涯海角，只愿为主公寻得如花美眷");
                this.lastTime = System.currentTimeMillis();
                ImageUtil.Setu setu = ImageUtil.getSetu(content.equalsIgnoreCase("ldst")?null
                        :content.replace("ldst ",""));

                if(setu!=null){
                    getWeChatClient().getWeChatCore().getHttpAPI().sendImage(fromUserName,setu.getFile());
                }else {
                    getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"风卷残云，美人如梦。臣竭尽全力，然佳人未现，望主公海涵");
                }
            }else if("checkupdate".equalsIgnoreCase(content)){
                GitHubRelease latestRelease = ImageUtil.getLatestRelease("meteorOSS", "WeChatBc");
                String text = latestRelease.getName()+"\n"+latestRelease.getBody();
                getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,text);
            }else if("plmm".equalsIgnoreCase(content)){
                if(!isPass()){
                    getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"“云有意，水无情。主公，频繁召唤易惊动清梦中的美人，容臣再寻良时。");
                    return;
                }
                getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"令箭已发，臣即刻遵命，启程寻觅美女佳人");
                try {
                    this.lastTime = System.currentTimeMillis();
                    File downloadedFile = downloadVideo("https://tucdn.wpon.cn/api-girl/index.php?wpon=json");
                    getWeChatClient().getWeChatCore().getHttpAPI().sendVideo(fromUserName,downloadedFile);
                    getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"风起云涌，佳人已至。主公，此乃天下绝色，愿此景令主公心旷神怡");
                } catch (Exception e) {
                    e.printStackTrace();
                    getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"行遍四海，却未能为主公找到所求之美女，愿主公海涵，臣再行寻觅");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // 创建一个文件夹用于存储setu
        this.file = new File(getWeChatClient().getDataFolder(),"setu");
        if(!file.exists()) file.mkdirs();
        this.patFile = new File(getWeChatClient().getDataFolder(),"pat");
        if(!patFile.exists()) patFile.mkdirs();
        INSTANCE = this;
        getWeChatClient().getEventManager().registerPluginListener(this,this);
        getCommand("setu").setCommandExecutor(new SetuCommand(getWeChatClient()));
    }

    @Override
    public void onDisable() {

    }
}
