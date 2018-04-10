package qq.server.view;
/*
服务器控制面板，管理服务器和监控用户
 */
import qq.server.back.MyQqServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import qq.server.back.*;

public class ControlPanel extends JFrame implements ActionListener {
    JPanel jp1;
    JButton jb1,jb2;

    public ControlPanel(){
        jp1 = new JPanel();
        jb1 = new JButton("启动服务器");
        jb1.addActionListener(this);
        jb2 = new JButton("关闭服务器");
        jp1.add(jb1);
        jp1.add(jb2);

        this.add(jp1);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,400);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==jb1){
            new MyQqServer();
        }
    }

    public static void main(String args[]){
        ControlPanel cp = new ControlPanel();
    }
}
