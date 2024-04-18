package qqclient.service;

//该类/对象，提供和消息相关的服务方法

import QQCommon.Message;
import QQCommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MessageClientService {
    //content 内容
    //senderId 发送用户的Id
    //getterId 接收用户的Id


    public void sendMessageToAll(String content, String senderId){
        //构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);//群发消息类型
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间设置到message对象
        System.out.println("你对大家说 " + content);
        //发送给服务端
        try {
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessageToOne(String content, String senderId, String getterId){
        //构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间设置到message对象
        System.out.println("你对 " + getterId + " 说 " + content);
        //发送给服务端
        try {
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
