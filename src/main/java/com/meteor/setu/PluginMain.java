package com.meteor.setu;

import com.meteor.setu.util.ImageUtil;
import com.meteor.wechatbc.entitiy.message.Message;
import com.meteor.wechatbc.event.EventHandler;
import com.meteor.wechatbc.impl.event.Listener;
import com.meteor.wechatbc.impl.event.sub.ReceiveMessageEvent;
import com.meteor.wechatbc.impl.model.MsgType;
import com.meteor.wechatbc.plugin.BasePlugin;
import lombok.Getter;

import java.io.File;

public class PluginMain extends BasePlugin implements Listener {

    @Getter private File file;

    public static PluginMain INSTANCE = null;

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
        if(last>=15) {
            lastTime = System.currentTimeMillis();
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
                File setu = ImageUtil.getSetu();
                if(setu!=null){
                    getWeChatClient().getWeChatCore().getHttpAPI().sendImage(fromUserName,setu);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // 创建一个文件夹用于存储setu
        this.file = new File(getWeChatClient().getDataFolder(),"setu");
        if(!file.exists()) file.mkdirs();
        INSTANCE = this;
        getWeChatClient().getEventManager().registerPluginListener(this,this);
    }

    @Override
    public void onDisable() {

    }
}
