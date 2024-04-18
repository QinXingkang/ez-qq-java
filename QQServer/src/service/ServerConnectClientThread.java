package service;



import QQCommon.Message;
import QQCommon.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;


//该类的一个对象和某个客户端保持通信
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;//连接到服务端的用户的Id

    public ServerConnectClientThread() {

    }

    public Socket getSocket() {
        return socket;
    }

    public ServerConnectClientThread(Socket socket, String userId){
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {//这里线程处于run的状态，可以发送/接收消息
        while (true){

            try {
                System.out.println("服务端和客户端" + userId + "保持通信，读取数据");
                ObjectInputStream objectInputStream = null;
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();
                //后面会使用message,根据message的类型，做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIENDS)){
                    //客户端要求在线用户列表
                    //在线用户列表按照 “100 200 300 至尊宝 紫霞仙子” 的形式存储

                    System.out.println(message.getSender()+ " 要在线用户列表");
                    String onlineUsers = ManageClientThreads.getOnlineUsers();
                    //返回message
                    //构建一个message对象，返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIENDS);
                    message2.setContent(onlineUsers);
                    message2.setGetter(message.getSender());
                    //写入到数据通道，返回给客户端
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(message2);
                }else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //需要遍历管理线程的集合，把所有的线程的socket得到，然后把message进行转发
                    HashMap<String,ServerConnectClientThread> hm = ManageClientThreads.getHm();

                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()){

                        //取出在线用户id
                        String onLineUserId = iterator.next().toString();

                        if (!onLineUserId.equals(message.getSender())){//排除群发给自己
                            //进行转发
                            ObjectOutputStream objectOutputStream =
                                    new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                            objectOutputStream.writeObject(message);
                        }
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){

                    System.out.println(message.getSender() + " 退出");
                    //将这个客户端对应的线程，从集合删除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();//关闭连接
                    //退出while循环
                    break;
                }else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //根据message获取getterId，然后再得到对应线程
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());

                    ObjectOutputStream objectOutputStream =
                            new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);

                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    //根据getterId 获取到对应的线程，将message对象转发
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);

                } else {
                    System.out.println("其他类型的message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
