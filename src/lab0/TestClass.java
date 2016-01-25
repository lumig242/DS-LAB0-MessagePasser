package lab0;

public class TestClass {
	public static void main(String[] args){
		final MessagePasser mp1 = new MessagePasser("test", "alice");
		Thread recieve = new Thread() {
            public void run() {
            	while(true){
            		Message msg = mp1.receive();
            		if(msg != null){
            			System.out.println("Recieve in the application" + msg);
            		}
            	}
            }
        };
        recieve.start();
        
        final MessagePasser mp2 = new MessagePasser("test", "bob");
		Thread send = new Thread() {
            public void run() {
                mp2.send(new Message("alice", "test", "testdata!!!!!"));
            }
        };
        send.start();
        
        
	}
}
