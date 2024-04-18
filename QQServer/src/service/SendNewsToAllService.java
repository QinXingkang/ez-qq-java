package service;

import QQCommon.Message;
import QQCommon.MessageType;
import qqclient.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class SendNewsToAllService implements Runnable{

    @Override
    public void run() {
        while (true){
            System.out.println("请输入服务器要推送的消息[输入exit表示退出推送服务]");
            String news = Utility.readString(100);
            if ("exit".equals(news)){
                break;
            }
            //构建一个消息，本质上是群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人说：" + news);

            //遍历当前所有的通信线程，得到socket并发送message

            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onLineUserId =  iterator.next();
                ServerConnectClientThread serverConnectClientThread = hm.get(onLineUserId);
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }
}
