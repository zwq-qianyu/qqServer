package qq.server.view;
/*
服务器控制面板，管理服务器和监控用户
 */
import javafx.beans.binding.BooleanExpression;
import qq.common.Message;
import qq.server.back.MyQqServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import qq.server.tools.*;
import qq.server.back.*;
import qq.server.back.ClientConSeverTread;


public class ControlPanel extends JFrame implements ActionListener {
    private JPanel jp1 = new JPanel();
    private JButton jb1 = new JButton("启动服务器");
    private JButton jb2 = new JButton("关闭服务器");
    private JPanel jp2 = new JPanel(new GridLayout(1,2));
    private JPanel jp3 = new JPanel(new GridLayout(1,2));
    private JButton jbt = new JButton("群送消息");
    private JButton jbt3 = new JButton("强制下线");
    private JTextField jtf = new JTextField();
    private JPanel jp_mid = new JPanel(new GridLayout(1,2));
    private JPanel jp4 = new JPanel();      //用户列表显示用的JPanel
    private JTextArea jta = new JTextArea();
    public static List lstUser = new List();

    public ControlPanel(){

        jb1.addActionListener(this);
        jb2.addActionListener(this);
        jp1.add(jb1);
        jp1.add(jb2);
        this.add(jp1,BorderLayout.NORTH);
        jp2.add(jtf);
        jp2.add(jp3);
        jp3.add(jbt);
        jp3.add(jbt3);
        jbt.addActionListener(this);    //监听群发功能
        jbt3.addActionListener(this);    //监听强制下线功能
        //this.add(jtf);

        jp4.add(lstUser);
        jp_mid.add(jta);
        jp_mid.add(jp4);
        this.initUserList();
        this.add(jp_mid,BorderLayout.CENTER);
        this.add(jp2,BorderLayout.SOUTH);
        jtf.setFont(new Font("宋体",Font.PLAIN,25));
        jta.setFont(new Font("宋体",Font.PLAIN,25));
        jta.setBackground(Color.cyan);
        jbt.addActionListener(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400,300);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==jb2){
            //dispose();
            System.exit(0);
        }
        if(e.getSource()==jb1){
            MyQqServer qs = new MyQqServer();
            qs.start();
        }

        //点击群发消息后发送
        if(e.getSource()==jbt){
            //显示信息
            String info = jtf.getText() + "\n\r";
            this.jta.append(info);
            //发送给所有在线用户
            ClientConSeverTread.notifyMesToOthers(info);
            //清空输入框
            this.jtf.setText("");
        }

        //点击强制下线后让对方
        if(e.getSource()==jbt3){
            //获取选择的用户
            String chooseUser = lstUser.getSelectedItem();
            Socket s = ManageClientTreads.getClientTread(chooseUser).getS();
            try{
                s.close();      //关闭socket

                //下面执行与用户主动下线相同的操作
                ManageClientTreads.delClientTread(chooseUser);  //将下线用户从hm哈希表中移除
                ControlPanel.lstUser.remove(chooseUser);
                System.out.println(chooseUser+" 已下线");
                //得到所有在线人的进程，告诉他们该用户下线了
                HashMap hm = ManageClientTreads.hm;
                Iterator it = hm.keySet().iterator();

                while(it.hasNext()){
                    Message m = new Message();
                    m.setCon(chooseUser);
                    m.setMesType(MessageType.message_ret_off_line);

                    //获得在线人的id
                    String onlie_user_id = it.next().toString();
                    try{
                        ObjectOutputStream oos = new ObjectOutputStream
                                (ManageClientTreads.getClientTread(onlie_user_id).getS().getOutputStream());
                        m.setGetter(onlie_user_id);
                        System.out.println(chooseUser+" 已下线2");
                        oos.writeObject(m);
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }

                //再次更新服务器端的用户列表显示
                this.initUserList();

            }catch (Exception err){
                System.out.println("强制下线失败！");
                err.printStackTrace();
            }
        }
    }

    //初始化用户列表
    public static void initUserList(){
        lstUser.removeAll();
        //lstUser.add("All");
        //lstUser.select(0);
        //得到所有在线人的进程
        HashMap hm = ManageClientTreads.hm;
        Iterator it = hm.keySet().iterator();
        while(it.hasNext()){
            //获得在线人的id
            String onlie_user_id = it.next().toString();
            lstUser.add(onlie_user_id);
        }
    }

    public static void main(String args[]){
        ControlPanel cp = new ControlPanel();
    }
}
