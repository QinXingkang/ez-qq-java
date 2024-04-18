package qqclient.view;


import qqclient.service.*;
import qqclient.utils.Utility;

import java.io.IOException;

//客户端的菜单界面
public class QQView {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new QQView().mainMenu();
        System.out.println("客户端退出系统");
    }

    private boolean loop = true;//控制是否显式菜单
    private String key = "";//接收用户的键盘输入
    private UserClientService userClientService = new UserClientService(); //对象时用于登录服务/注册用户

    private MessageClientService messageClientService = new MessageClientService();//对象用户私聊/群聊

    private FileClientService fileClientService = new FileClientService();//该对象用于传输文件

    //显式主菜单
    private void mainMenu() throws IOException, ClassNotFoundException {
        while (loop) {
            System.out.println("============欢迎登录网络通信系统============");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择：");

            key = Utility.readString(1);

            //根据用户的输入，来处理不同的逻辑
            switch (key) {
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密码：");
                    String passwd = Utility.readString(50);

                    //接下来需要到服务端验证登录的用户是否合法
                    //这里有很多代码，我们这里编写一个类UserClientService[用户登录/注册]
                    if (userClientService.checkUser(userId, passwd)) {
                        System.out.println("============欢迎 (用户" + userId + "登陆成功）============");
                        //进入到二级菜单
                        while (loop) {
                            System.out.println("\n============网络通信系统二级菜单（用户 " + userId + ")===============");
                            System.out.println("\t\t 1 显式在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");

                            System.out.print("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    //这里准备写一个方法，来获取在线用户列表
                                    userClientService.onlineFriendsList();
                                    System.out.println("显式在线用户列表");
                                    break;
                                case "2":
                                    System.out.println("请输入想对大家说的话：");
                                    String s = Utility.readString(100);
                                    //调用一个方法，将消息封装成message对象，发送给服务端
                                    messageClientService.sendMessageToAll(s,userId);
                                    break;
                                case "3":
                                    System.out.print("请输入向想要聊天的用户号：");
                                    String getterId = Utility.readString(50);
                                    System.out.println("请输入想说的话：");
                                    String content = Utility.readString(100);
                                    //编写一个方法，将消息发送给服务器端
                                    messageClientService.sendMessageToOne(content, userId, getterId);
                                    System.out.println("私聊消息");
                                    break;
                                case "4":
                                    System.out.print("请输入你想要发送文件的用户（在线）");
                                    getterId = Utility.readString(50);
                                    System.out.print("请输入你要发送文件的路径（D:\\xx.jpg）");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入对方接收文件的路径（E:\\xx.jpg）");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src,dest,userId,getterId);
                                    break;
                                case "9":
                                    //调用方法，给服务器发送一个退出系统的message
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    } else {
                        //登录服务器失败
                        System.out.println("==============登录失败==============");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
