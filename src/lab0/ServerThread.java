package lab0;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The listening server thread
 * @author LumiG
 *
 */
public class ServerThread implements Runnable {
    private Socket client;
    private ConfigParser config;
    LinkedBlockingQueue<Message> receiveMsgs = new LinkedBlockingQueue<Message>();
    
    public ServerThread(Socket client, LinkedBlockingQueue<Message> receiveMsgs, ConfigParser config){  
        this.client = client;  
        this.receiveMsgs = receiveMsgs;
        this.config = config;
    }
    
    /**
     * First check if this is the first message received
     * If so, store the output, input stream 
     * Put all the msgs received into a msg queue
     */
    @Override  
    public void run() {  
    	Boolean firstMsgFlag = true;
        try{     
        	ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        	ObjectInputStream input = new ObjectInputStream(client.getInputStream());
        	
            while(true){  
                Message msg =  (Message) input.readObject();

                if(firstMsgFlag){
                	Server s = config.getServer(msg.getSource());
					System.out.println("Connected client!  " + s);
                	s.setOutput(output);
					s.setInput(input);
                	//output.writeObject(new Message(null, null, s));
                	firstMsgFlag = false;
                }
                System.out.println(msg + "receive");
                receiveMsgs.put(msg);

            }    
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    
    protected void finalize(){
    	try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}


