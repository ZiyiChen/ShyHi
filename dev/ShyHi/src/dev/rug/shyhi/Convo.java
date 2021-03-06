package dev.rug.shyhi;

import java.util.ArrayList;

import android.util.Log;

public class Convo {

	private String _id;
	private String _rev;
	private String type;
	private String user1;
	private String user2;
	private ArrayList<Message> messages;
	
	
	public Convo(){};
	public Convo(String i, String u1, String u2, ArrayList<Message> m){
		_id = i;
		_rev = "";
		user1 = u1;
		user2 = u2;
		messages = m;
	}
	public Convo(String i, String r, String u1, String u2, ArrayList<Message> m){
		_id = i;
		_rev = r;
		user1 = u1;
		user2 = u2;
		messages = m;
	}
	
	public String getId(){
		return _id;
	}
	public String getRev(){
		return _rev;
	}
	public String getType(){
		return type;
	}
	public String getUser1(){
		return user1;
	}
	public String getUser2(){
		return user2;
	}
	public String getOtherUser(String user){
		if(getUser1().equals(user)){
			Log.i("retUser",getUser2());
			return getUser2();
		}
		else{
			Log.i("retUser",getUser1());
			return getUser1();
		}
	}
	public ArrayList<Message> getMessages(){
		return messages;
	}
	
	public void addMessage(Message m){
		messages.add(m);
	}
	
	public String getMostRecentMessage(){
		return messages.get(messages.size()-1).getMessage();
	}
	public String getMsgsStr(){
		ArrayList<Message> msgs = getMessages();
		String retStr = "";
		for(int i = 0; i < msgs.size(); ++i){
			if(i == msgs.size()-1)
				retStr = retStr + msgs.get(i).messageToString();
			else
				retStr = retStr + msgs.get(i).messageToString() +",";
		
		}
		return retStr;
	}
	public String toStringForPut(){
		String convoStr = "{"+"\"_id\":"+getId()+","+
				"\"_rev\":"+getRev()+","+
				"\"type\":"+"\"convo\","+
				"\"user1\":"+getUser1()+","+
				"\"user2\":"+getUser2()+","+
				"\"messages\": ["+getMsgsStr()+"]}";
		return convoStr;				
	}
	
}
