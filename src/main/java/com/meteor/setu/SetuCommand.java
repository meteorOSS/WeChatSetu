package com.meteor.setu;

import com.meteor.wechatbc.HttpAPI;
import com.meteor.wechatbc.command.CommandExecutor;
import com.meteor.wechatbc.command.sender.CommandSender;
import com.meteor.wechatbc.command.sender.ConsoleSender;
import com.meteor.wechatbc.command.sender.ContactSender;
import com.meteor.wechatbc.entitiy.contact.Contact;
import com.meteor.wechatbc.impl.WeChatClient;

import java.util.ArrayList;
import java.util.List;

public class SetuCommand implements CommandExecutor {

    // 把客户端对象注入进来
    private WeChatClient weChatClient;

    public SetuCommand(WeChatClient weChatClient){
        this.weChatClient = weChatClient;
    }


    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {

        // 是否有权限 (控制台执行)
        boolean isPass = commandSender instanceof ConsoleSender;

        if(isPass){
            int length = strings.length;
            if(length !=2 ){
                commandSender.sendMessage("[色图插件] 参数错误!");
                return;
            }
            // 取得指令参数
            String opt = strings[0];
            String v = strings[1];
            // 存储进设置里
            Options.optionsMap.put(opt,v);
            commandSender.sendMessage(String.format("[色图插件] 已更改选项 %s=%s",opt,v));
        }else {
            commandSender.sendMessage("[色图插件] 没有足够的权限");
        }

    }
}
