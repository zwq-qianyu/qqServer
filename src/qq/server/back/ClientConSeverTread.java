/*
服务器与某个客户端的通信线程
 */
package qq.server.back;

import qq.common.Message;
import qq.server.tools.MessageType;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class ClientConSeverTread extends Thread implements MessageType {
    Socket s;

    public ClientConSeverTread(Socket s){
        //把服务器和该客户端的连接赋给s
        this.s = s;
    }

    //当有一个新的用户上线时，进程通知其他人
    public void notifyOthers(String who){
        //得到所有在线人的进程
        HashMap hm = ManageClientTreads.hm;
        Iterator it = hm.keySet().iterator();

        while(it.hasNext()){
            Message m = new Message();
            m.setCon(who);
            m.setMesType(MessageType.message_ret_onlineFriends);

            //获得在线人的id
            String onlie_user_id = it.next().toString();
            try{
                ObjectOutputStream oos = new ObjectOutputStream
                        (ManageClientTreads.getClientTread(onlie_user_id).s.getOutputStream());
                m.setGetter(onlie_user_id);
                oos.writeObject(m);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //当群发消息时，进程通知所有在线人员
    public static void notifyMesToOthers(String mes){
        //得到所有在线人的进程
        HashMap hm = ManageClientTreads.hm;
        Iterator it = hm.keySet().iterator();

        while(it.hasNext()){
            Message m = new Message();
            m.setCon(mes);
            m.setMesType(MessageType.message_to_all);

            //获得在线人的id
            String onlie_user_id = it.next().toString();
            try{
                ObjectOutputStream oos = new ObjectOutputStream
                        (ManageClientTreads.getClientTread(onlie_user_id).s.getOutputStream());
                m.setGetter(onlie_user_id);
                oos.writeObject(m);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Socket getS() {
        return s;
    }

    public void run(){
        while(true){
            //这里该线程接收客户端的信息
            try{
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                Message ms = (Message)ois.readObject();

                //System.out.println(ms.getSender()+"对"+ms.getGetter()+"说："+ms.getCon());

                //对从客户端获得的消息进行类型判断，然后做出相应的处理
                if(ms.getMesType().equals(MessageType.message_common_mes)){
                    //取得通话对象的通信线程
                    ClientConSeverTread ccst = ManageClientTreads.getClientTread(ms.getGetter());
                    ObjectOutputStream oos = new ObjectOutputStream(ccst.s.getOutputStream());
                    oos.writeObject(ms);
                }
                else if(ms.getMesType().equals(MessageType.message_off_line)){
                    ManageClientTreads.delClientTread(ms.getSender());  //将下线用户从hm哈希表中移除
                    System.out.println(ms.getSender()+" 已下线");
                    //得到所有在线人的进程，告诉他们该用户下线了
                    HashMap hm = ManageClientTreads.hm;
                    Iterator it = hm.keySet().iterator();

                    while(it.hasNext()){
                        Message m = new Message();
                        m.setCon(ms.getSender());
                        m.setMesType(MessageType.message_ret_off_line);

                        //获得在线人的id
                        String onlie_user_id = it.next().toString();
                        try{
                            ObjectOutputStream oos = new ObjectOutputStream
                                    (ManageClientTreads.getClientTread(onlie_user_id).s.getOutputStream());
                            m.setGetter(onlie_user_id);
                            System.out.println(ms.getSender()+" 已下线2");
                            oos.writeObject(m);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    System.out.println(ms.getSender()+" 已下线3");
                    //最后关闭与这个用户的socket，这能是程序更加健壮   不然会报EOFError
                }
                else if(ms.getMesType().equals(MessageType.message_get_onlineFriends)){
                    System.out.println(ms.getSender()+" 请求他的好友列表");
                    //把在线的用户列表返回给客户端
                    String ret = ManageClientTreads.getAllOnlineUsers();
                    Message m = new Message();
                    m.setMesType(MessageType.message_ret_onlineFriends);
                    m.setCon(ret);
                    m.setGetter(ms.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                    oos.writeObject(m);
                }


            }catch (Exception e){
                //e.printStackTrace();
            }finally {

            }
        }
    }

}
