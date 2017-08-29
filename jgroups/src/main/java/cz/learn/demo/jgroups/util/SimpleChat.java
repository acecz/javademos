package cz.learn.demo.jgroups.util;

import java.util.TreeMap;
import java.util.UUID;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class SimpleChat extends ReceiverAdapter {
    public static final TreeMap<Long, Object> msgMap = new TreeMap<>();
    public static final String id = UUID.randomUUID().toString();
    private JChannel channel;
    private static final SimpleChat sc = new SimpleChat();

    private SimpleChat() {
    }

    public static SimpleChat getInstance() {
        if (sc.channel == null) {
            try {
                sc.start();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return sc;
    }

    private void start() throws Exception {
        channel = new JChannel().setReceiver(sc); // use the default config, udp.xml
        channel.connect("TestCluster");
    }

    public static void main(String[] args) throws Exception {
        getInstance().start();
    }

    public static void startup() throws Exception {
        new SimpleChat().start();
    }

    public void sendMsg(String content) throws Exception {
        Message msg = new Message(null, id + "#" + content);
        channel.send(msg);
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    @Override
    public void receive(Message msg) {
        if (msgMap.size() > 5) {
            msgMap.pollFirstEntry();
            msgMap.pollFirstEntry();
        }
        msgMap.put(System.currentTimeMillis(), msg.getObject());
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }
}
