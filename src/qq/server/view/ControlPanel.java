package qq.server.view;
/*
服务器控制面板，管理服务器和监控用户
 */
import javafx.beans.binding.BooleanExpression;
import qq.server.back.MyQqServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import qq.server.back.*;

public class ControlPanel extends JFrame implements ActionListener {
    private JPanel jp1 = new JPanel();
    private JButton jb1 = new JButton("启动服务器");
    private JButton jb2 = new JButton("关闭服务器");
    private JPanel jp2 = new JPanel();
    private JButton jbt = new JButton("发送消息");
    private JTextField jtf = new JTextField();
    private JTextArea jta = new JTextArea();

    public ControlPanel(){
        jb1.addActionListener(this);
        jb2.addActionListener(this);
        jp1.add(jb1);
        jp1.add(jb2);
        this.add(jp1,BorderLayout.NORTH);
        jp2.add(jtf);
        jp2.add(jbt);
        //this.add(jtf);
        this.add(jta,BorderLayout.CENTER);
        this.add(jp2,BorderLayout.SOUTH);
        jtf.setFont(new Font("宋体",Font.PLAIN,25));
        jta.setFont(new Font("宋体",Font.PLAIN,25));
        jta.setBackground(Color.pink);
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
            new MyQqServer();
        }
    }

    public static void main(String args[]){
        ControlPanel cp = new ControlPanel();
    }
}
