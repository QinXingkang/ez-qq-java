package qqclient.service;
//该类完成用户登录验证和用户注册等功能



import QQCommon.Message;
import QQCommon.MessageType;
import QQCommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class UserClientService {
    //因为我们可能在其他地方使用User信息，因此做成成员属性
    private static User u = new User();

    //因为Socket在其他地方也可能使用，因此也做成属性
    private Socket socket;
    //根据userId和passwd到服务器验证该用户是否合法



    public boolean checkUser(String userId, String passwd) throws IOException, ClassNotFoundException {
        boolean b = false;
        //创建User对象
        u.setUserId(userId);
        u.setPasswd(passwd);

        //连接到服务器，发送u对象
        socket = new Socket(InetAddress.getByName("LAPTOP-I43ULS42"), 9999);
        //得到ObjectOutputStream对象
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(u);//发送user对象

        //读取从服务端回复的Message对象
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Message ms = (Message) objectInputStream.readObject();

        if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED )){//登录成功

            //创建一个和服务器端保持通讯的线程->创建一个线程类 ClientConnectServerThread
            ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
            //启动客户端的线程
            clientConnectServerThread.start();

            //这里为了后面客户端的扩张，我们将线程放入到集合中管理
            ManageClientConnectServerThread.addClientConnectServerThread(userId,clientConnectServerThread);
            b = true;


        }else {//登陆失败
            //如果登录失败，我们就不能启动和服务器通信的线程，关闭socket
            socket.close();
        }
        return b;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendsList(){
        //发送一个Message，类型MESSAGE_GET_ONLINE_FRIENDS
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIENDS);
        message.setSender(u.getUserId());

        //发送给服务器
        //应该得到当前线程的socket对应的ObjectOutputStream对象
        try {
            //先从管理线程的集合中，通过userId得到线程对象，再通过这个线程得到关联的Socket，再通过Socket对应的ObjectOutputStream
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);//发送一个Message对象，向服务端要求在线用户列表
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //编写方法退出客户端，并给服务端发送一个退出系统的message
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定是哪个调用退出

//        //方法一：
//        //发送Message
//        try {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            objectOutputStream.writeObject(message);
//            System.out.println(u.getUserId() + " 退出系统");
//            System.exit(0);//结束进程
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        //方法二：
        try {
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
            System.out.println(u.getUserId() + " 退出系统 ");
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
