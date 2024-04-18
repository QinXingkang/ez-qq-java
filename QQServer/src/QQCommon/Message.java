package QQCommon;

import java.io.Serializable;

//表示用户端和服务端通信时的消息对象
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;//发送方
    private String getter;//接收方
    private String content;//消息内容
    private String sendTime;//发送时间
    private String mesType;//消息类型（在接口定义消息类型）

    //进行扩展和文件相关的成员
    private byte[] fileBytes;
    private int fileLen = 0;
    private String dest;//文件接收地
    private String src;//文件发出地

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public int getFileLen() {
        return fileLen;
    }

    public void setFileLen(int fileLen) {
        this.fileLen = fileLen;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getMesType() {
        return mesType;
    }

    public void setMesType(String mesType) {
        this.mesType = mesType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
