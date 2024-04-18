package service;

import QQCommon.Message;
import QQCommon.MessageType;
import QQCommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

//这是服务端，在监听9999，等待客户端的连接，并保持通信
public class QQServer {
    private ServerSocket serverSocket = null;

    //创建一个集合存放多个用户，如果是这些用户登录，就认为是合法的
    //这里我们也可以使用 ConcurrentHashMap，可以处理并发的集合，没有线程安全问题
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ArrayList<Message>> offLineDb = new ConcurrentHashMap<>();

    static {//在静态代码块，初始化validUsers
        validUsers.put("100", new User("100","123456"));
        validUsers.put("200", new User("200","123456"));
        validUsers.put("300", new User("300","123456"));
        validUsers.put("至尊宝", new User("至尊宝","123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子","123456"));
    }

    //验证用户是否有效的方法
    private boolean checkUser(String userId, String passwd){
        User user = validUsers.get(userId);
        if (user == null){//说明userId没有存在validUsers 的key中
            return false;
        }
        if (!user.getPasswd().equals(passwd)){//userId正确，但是密码错误
            return false;
        }
        return true;
    }

    public QQServer() throws IOException {
        //注意：端口可以写在配置文件
        try {
            System.out.println("服务端在9999端口监听");

            //启动推送新闻的线程
            SendNewsToAllService service = new SendNewsToAllService();
            new Thread(service).start();

            serverSocket = new ServerSocket(9999);

            while (true){//当和某个客户端建立连接后，会继续监听，等待其他的连接
                Socket socket = serverSocket.accept();//如果没有客户端连接，就会阻塞在这里
                //得到socket关联的对象输入流
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                User u = (User) objectInputStream.readObject();

                //得到socket关联的对象输出流
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                //创建一个Message对象，准备回复客户端
                Message message = new Message();
                //验证
                if (checkUser(u.getUserId(),u.getPasswd())){
                    //登陆成功
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复给客户端
                    objectOutputStream.writeObject(message);
                    //创建一个线程，和客户端保持通信，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动该线程
                    serverConnectClientThread.start();
                    //把该线程对象放入到一个集合中进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);



                }else {
                    //登录失败
                    System.out.println("用户 id=" + u.getUserId() + " passwd=" + u.getPasswd() + " 验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAILED);
                    objectOutputStream.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            //如果服务器推出了while循环，说明服务器端不再监听，因此关闭ServerSocket
            serverSocket.close();
        }
    }
}
