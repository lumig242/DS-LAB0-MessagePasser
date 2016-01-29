package core;

import java.util.concurrent.LinkedBlockingQueue;

import config.ConfigParser;
import config.Message;
import config.Rule;
import config.Server;

/**
 * The listening server thread
 * @author LumiG
 *
 */
public class ListenerThread implements Runnable {
    private Server server;
    LinkedBlockingQueue<Message> receiveMsgs = new LinkedBlockingQueue<Message>();
    private LinkedBlockingQueue<Message> delayReceiveMsgs = new LinkedBlockingQueue<Message>();
    ConfigParser config;
    
    public ListenerThread(Server server, LinkedBlockingQueue<Message> receiveMsgs, LinkedBlockingQueue<Message> delayReceiveMsgs, ConfigParser config){  
        this.server = server;  
        this.receiveMsgs = receiveMsgs;
        this.delayReceiveMsgs = delayReceiveMsgs;
        this.config = config;
    }
    
    /**
     * Put all the msgs received into a msg queue
     */
    @Override  
    public void run() {  
    	try{         	
            while(true){  
            	Message msg = (Message) server.getInput().readObject();
            	Rule rule = config.matchSendRule(msg.getSource(), msg.getDest(), msg.getKind(), msg.get_seqNum());
            	if(rule==null) {
	                System.out.println(msg + "receive");
	                receiveMsgs.put(msg);
	                System.out.println(receiveMsgs);
	                
	                while(!delayReceiveMsgs.isEmpty()) {
	                	receiveMsgs.put(delayReceiveMsgs.poll());
	                }
            	} else {
            		switch(rule.getKind().toLowerCase()) {
            			case "drop" : {;}
            			case "dropafter" : {;}
            			case "delay" : {
            				delayReceiveMsgs.put(msg);
            			}
            		}
            	}
                
            }    
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    
    
}


