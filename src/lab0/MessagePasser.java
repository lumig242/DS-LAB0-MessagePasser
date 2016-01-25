package lab0;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The main class
 * @author LumiG
 *
 */
public class MessagePasser {
	private Server localServer;
	private LinkedBlockingQueue<Message> sendMsgs = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Message> receiveMsgs = new LinkedBlockingQueue<Message>();
	ConfigParser config ;
	
	public MessagePasser(String configuration_filename, String local_name){
		// Parse the Yaml configuration file
		config = new ConfigParser(configuration_filename);
		localServer = config.getServer(local_name);
		
		// Start the thread to keep listening on port
		// Start separate thread for all the clients coonected
		Thread t = new Thread(new Runnable() {	
			@SuppressWarnings("resource")
			@Override
			public void run() {
				try {
					ServerSocket server = new ServerSocket(localServer.getPort());
					System.out.println("Listening on " + localServer);
					Socket client = null;
					while(true){
						// Client connected
						client = server.accept();  
						// Set the input output stream for this node
			        	ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
			        	ObjectInputStream input = new ObjectInputStream(client.getInputStream());
			        	// We have to read the first message to get the name of client
			        	Message msg =  (Message) input.readObject();
			        	Server s = config.getServer(msg.getSource());
			        	System.out.println("Connected client!  " + s);
	                	s.setOutput(output);
						s.setInput(input);
						// Put the first message in the queue
						receiveMsgs.put(msg);
						// Start a new thread to listen from the node
						new Thread(new ListenerThread(s, receiveMsgs)).start();
					}
				} catch (IOException | ClassNotFoundException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		
		// Start a consumer thread to send all the messages in the queue
		new Thread(new SendConsumerThread(sendMsgs, config)).start();
		
	}
	
	/**
	 * Send a single messages
	 * First check if the tcp connection is already established
	 * Put the message in the queue, acts as a producer
	 * @param message
	 */
	void send(Message message){
		//message.set_seqNum(sequenceNumber++);
		message.set_source(localServer.getName());
		//System.out.println("Sent: " + message);
		Server destServer = config.getServer(message.getDest());
		//System.out.println("Destserver: " + destServer);
		
		// if this is the first msg sent
		// act as the client
		if(destServer.getOutput() == null){
			try {
				System.out.println("Connect to Destserver: " + destServer);
				@SuppressWarnings("resource")
				Socket socket = new Socket(destServer.getIp(), destServer.getPort());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				destServer.setInput(inputStream);
				destServer.setOutput(outputStream);
				new Thread(new ListenerThread(destServer, receiveMsgs)).start();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		
		// Put the msg into queue
		try {
			sendMsgs.put(message);
			//System.out.println("Message Passer" + sendMsgs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Message receive(){
		if(receiveMsgs.isEmpty()){
			//System.out.println("No messages received!");
			return null;
		}
		Message msg = receiveMsgs.poll();
		return msg;
	};
}
