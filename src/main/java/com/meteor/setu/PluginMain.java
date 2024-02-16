package com.meteor.setu;

import com.meteor.setu.model.GitHubRelease;
import com.meteor.setu.util.ImageUtil;
import com.meteor.setu.util.PatPatTool;
import com.meteor.wechatbc.entitiy.contact.Contact;
import com.meteor.wechatbc.entitiy.message.Message;
import com.meteor.wechatbc.event.EventHandler;
import com.meteor.wechatbc.impl.event.Listener;
import com.meteor.wechatbc.impl.event.sub.ReceiveMessageEvent;
import com.meteor.wechatbc.impl.model.MsgType;
import com.meteor.wechatbc.plugin.BasePlugin;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

import static com.meteor.setu.util.VideoDownloader.downloadVideo;

public class PluginMain extends BasePlugin implements Listener {

    @Getter private File file;

    public static PluginMain INSTANCE = null;
    private File patFile;

    @Override
    public void setEnable(boolean b) {
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public void onLoad() {

    }

    private long lastTime = 0L;

    // 15s可调用一次
    public boolean isPass(){
        long last = (System.currentTimeMillis() - lastTime) / 1000;
        if(last>=12) {
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
            if("无内鬼".equalsIgnoreCase(content)){
                if(!isPass()) {
                    getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"调用的太频繁了!");
                    return;
                }
                this.lastTime = System.currentTimeMillis();
                File setu = ImageUtil.getSetu();
                if(setu!=null){
                    getWeChatClient().getWeChatCore().getHttpAPI().sendImage(fromUserName,setu);
                }
            }else if("checkupdate".equalsIgnoreCase(content)){
                GitHubRelease latestRelease = ImageUtil.getLatestRelease("meteorOSS", "WeChatBc");
                String text = latestRelease.getName()+"\n"+latestRelease.getBody();
                getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,text);
            }else if(isPai(message.getContent())){

                System.out.println("为拍一拍消息");

                String toUserName = message.getToUserName();
                File icon = getWeChatClient().getWeChatCore().getHttpAPI().getIcon(toUserName);

                Contact contact = getWeChatClient().getContactManager().getContactCache().get(toUserName);


                try {
                    String user = content!=null?contact.getNickName():toUserName;
                    PatPatTool.getPat(user,
                            icon,patFile,80);

                    File toImg = new File(patFile, String.format("%s_pat.gif", user));

                    getWeChatClient().getWeChatCore().getHttpAPI().sendImage(fromUserName,toImg);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }else if("来点烧鸡".equalsIgnoreCase(content)){
                if(!isPass()){
                    getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"色色色，千古一祸!主公要适可而止啊!");
                    return;
                }
                getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(fromUserName,"主公且在此等候，川这就为你寻找烧鸡");
                try {
                    this.lastTime = System.currentTimeMillis();
                    File downloadedFile = downloadVideo("https://tucdn.wpon.cn/api-girl/index.php?wpon=json");
                    getWeChatClient().getWeChatCore().getHttpAPI().sendVideo(fromUserName,downloadedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private boolean isPai(String content){
        return content.contains("拍了拍") || content.contains("拍拍");
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
    }

    @Override
    public void onDisable() {

    }
}
