package qqclient.service;


import QQCommon.Message;
import QQCommon.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

//该类完成 文件传输服务
public class FileClientService {
    //src 源文件
    //dest 目标文件
    //senderId 发送者
    //getterId 接收者
    public void sendFileToOne(String src, String dest, String senderId, String getterId) throws IOException {
        //读取src文件 -》封装message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src文件读入到程序的字节数组
            //将文件对应的字节数组设置到message对象
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if (fileInputStream == null){
                fileInputStream.close();
            }
        }
        //提示信息
        System.out.println("\n" + senderId + " 给 " + getterId + " 发送文件：" + src + " 到对方的电脑的目录 " + dest);

        //发送
        ObjectOutputStream objectOutputStream =
                new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
        objectOutputStream.writeObject(message);
    }
}
