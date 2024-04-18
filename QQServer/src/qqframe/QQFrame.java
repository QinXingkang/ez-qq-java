package qqframe;

import service.QQServer;

import java.io.IOException;

//该类创建QQServer，启动后台的服务
public class QQFrame {
    public static void main(String[] args) throws IOException {
        new QQServer();
    }
}
