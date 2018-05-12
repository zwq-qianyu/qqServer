package qq.server.back;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import qq.common.*;
import java.awt.*;

public class MyQqServer extends Thread{
    //private List userList = new java.awt.List();
    public MyQqServer(){

    }

    public void run(){
        try {
            //在9000端口监听
            System.out.println("服务器正在9000端口监听...");
            ServerSocket ss = new ServerSocket(9000);
            while(true){
                //阻塞，等待连接
                Socket s = ss.accept();

                //接受客户端发来的消息
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                User u = (User)ois.readObject();
                System.out.println("服务器接收到用户id: "+u.getUserId()+" 密码："+u.getPasswd());
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                Message m = new Message();

                if(u.getPasswd().equals("123456")){

                    m.setMesType("1");
                    //userList.add(u.getUserId());
                    //m.setUserlist(userList);   //更新登录用户列表
                    oos.writeObject(m);

                    System.out.println("连接成功");

                    //这里单开一个线程，让它与该客户端通讯
                    ClientConSeverTread ccst = new ClientConSeverTread(s);
                    //将线程添加到Hashmap中
                    ManageClientTreads.addClientTread(u.getUserId(),ccst);
                    //启动与该客户端通讯的线程
                    ccst.start();

                    //通知其他用户有新用户上线
                    ccst.notifyOthers(u.getUserId());
                }
                else{
                    System.out.println("密码错误");
                    m.setMesType("2");
                    oos.writeObject(m);
                    //关闭连接
                    s.close();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{

        }
    }

    public static void main(String args[]){

    }

}
