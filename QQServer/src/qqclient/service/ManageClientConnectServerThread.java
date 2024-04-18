package qqclient.service;

import java.util.HashMap;

//该类管理客户端连接到服务器端的线程的类
public class ManageClientConnectServerThread {
    //我们把多个线程放入到一个HashMap集合，key就是用户id，value就是线程
    public static HashMap<String,ClientConnectServerThread> hm = new HashMap<>();

    //将某个线程加入到集合中
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread){
        hm.put(userId,clientConnectServerThread);
    }
    //通过userId 可以得到对应线程
    public static  ClientConnectServerThread getClientConnectServerThread(String userId){
        return hm.get(userId);
    }
}
