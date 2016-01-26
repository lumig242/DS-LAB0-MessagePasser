package config;

import java.io.Serializable;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dest, source, kind;
	private Object data;
	private int sequenceNumber;
	
	@Override
	public String toString() {
		return "Message " +  sequenceNumber +
			   " dest@" + dest + 
			   " source@" + source +
			   " data@" + data;
	};
	
	public Message(String dest, String kind, Object data){
		this.dest = dest;
		this.kind = kind;
		this.data = data;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getDest() {
		return dest;
	}

	public void set_source(String source){
		this.source = source;
	};
	
	public void set_seqNum(int sequenceNumber){
		this.sequenceNumber = sequenceNumber;
	}

	public String getKind() {
		return kind;
	}
	
	
}
