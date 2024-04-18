package qqclient.service;



import QQCommon.Message;
import QQCommon.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;


public class ClientConnectServerThread extends Thread{
    //该线程需要持有Socket
    private Socket socket;

    //构造器可以接收一个Socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    //
    @Override
    public void run() {
        //因为Thread需要在后台和服务器通信，因此我们用While循环
        while(true){
            //不断读取服务器端的信息
            try {
                System.out.println("客户端线程，等待读取从服务器端发送的消息");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //如果服务器没有发送Message对象，线程会阻塞在这里
                Message message = (Message) objectInputStream.readObject();
                //注意，后面我们需要去使用message
                //判断这个message类型，然后做相应的业务处理
                //如果读取到的是 服务端返回的在线用户列表
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIENDS)){
                    //取出在线列表信息，并显式
                    //规定在线用户列表用“ ” 隔开表示
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n======当前在线用户列表=======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户：" + onlineUsers[i]);
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){//普通的聊天消息
                    //把从服务器端转发的消息显式到控制台即可
                    System.out.println("\n" + message.getSender()
                            + " 对你说： " + message.getContent());

                }else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){//如果是文件消息

                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter() +
                            " 发文件 " + message.getSrc() + " 到我的电脑目录 " + message.getDest());
                    //取出message的文件字节数组，通过文件输出流写到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getGetter());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n 保存文件成功");


                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    //显式在客户端的控制台
                    System.out.println("\n" + message.getSender() + "对你说" + message.getContent());
                } else {
                    System.out.println("是其他类型的message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    //为了更方便得到Socket，提供一个get方法

    public Socket getSocket() {
        return socket;
    }
}
