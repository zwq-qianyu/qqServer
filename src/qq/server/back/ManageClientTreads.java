package qq.server.back;

import java.util.HashMap;
import java.util.Iterator;

public class ManageClientTreads {

    //用散列来存放用户名及其线程，借此保存其s
    public static HashMap hm = new HashMap<String,ClientConSeverTread>();   //静态---管理端只需要一个Hashmap就足够了

    //向hm中添加一个客户端线程
    public static void addClientTread(String uid,ClientConSeverTread uthread){
        hm.put(uid,uthread);
    }

    //删除hm中一个客户端线程
    public static void delClientTread(String uid){
        hm.remove(uid);
    }

    public static ClientConSeverTread getClientTread(String uid){
        return (ClientConSeverTread)hm.get(uid);
    }

    public static String getAllOnlineUsers(){
        //使用迭代器获取全部在线用户
        Iterator it = hm.keySet().iterator();
        String ret = "";
        while(it.hasNext()){
            ret += it.next().toString()+" ";
        }
        return ret;
    }
}
