package lab0;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The listening server thread
 * @author LumiG
 *
 */
public class ListenerThread implements Runnable {
    private Server server;
    LinkedBlockingQueue<Message> receiveMsgs = new LinkedBlockingQueue<Message>();
    
    public ListenerThread(Server server, LinkedBlockingQueue<Message> receiveMsgs){  
        this.server = server;  
        this.receiveMsgs = receiveMsgs;
    }
    
    /**
     * Put all the msgs received into a msg queue
     */
    @Override  
    public void run() {  
    	try{         	
            while(true){  
            	Message msg = (Message) server.getInput().readObject();
                System.out.println(msg + "receive");
                receiveMsgs.put(msg);
                System.out.println(receiveMsgs);
            }    
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    
    
}


