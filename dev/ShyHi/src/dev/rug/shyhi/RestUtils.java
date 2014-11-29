package dev.rug.shyhi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;


//helper utility class for calling rest and getting json objects
public class RestUtils {

	public RestUtils(){};
	
	//It works!
	public Convo getConvoById(String convoID){
		//String convoStr = 
    	String convoStr = "";
    	Convo convo = null;
		try {
			convoStr = new fetchJSON().execute("http://104.236.22.60:5984/shyhi/_design/conversation/_view/get_convo?key=%22"+convoID+"%22").get();
			JsonParser jp = new JsonParser();
			JsonElement convoJ = jp.parse(convoStr);
			JsonArray convoRow = (JsonArray) convoJ.getAsJsonObject().get("rows");
			JsonElement item = convoRow.get(0).getAsJsonObject();
			JsonObject convoJSON = (JsonObject) item.getAsJsonObject().get("value");
			convo = getConvoFromJson(convoJSON);
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return convo;
	}

	//It works! 
	public ArrayList<Convo> getAllConvos(String key){
    	ArrayList<Convo> convoArr = new ArrayList<Convo>();
		String allConvosStr = "";
		try {
			allConvosStr = new fetchJSON().execute("http://104.236.22.60:5984/shyhi/_design/conversation/_view/get_all_convo?key=%22"+key+"%22").get();
			JsonParser jp = new JsonParser();
			JsonElement convos = jp.parse(allConvosStr);
			JsonArray convosArr = (JsonArray) convos.getAsJsonObject().get("rows");
			Log.i("Array",convosArr.toString());
			for(int i = 0; i < convosArr.size(); ++i){
				  JsonElement item = convosArr.get(i).getAsJsonObject();
				  JsonObject convo = (JsonObject) item.getAsJsonObject().get("value");
				  convoArr.add(getConvoFromJson(convo));
				  Log.i("convo",convo.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i("getAllConvos",allConvosStr);
    	//turn the allConvosStr into a java object
    	return convoArr;
    }
	
	public void updateConvo(Convo convo){
		//send convo to couchdb and update
	}
	
	public Convo getConvoFromJson(JsonObject convoObj){
		JsonArray msgArr = (JsonArray)convoObj.getAsJsonObject().get("messages");
		ArrayList<Message> msgArrList = new ArrayList<Message>();
		for(int i = 0; i < msgArr.size(); ++i){
			Message msg = getMessageFromJson(msgArr.get(i).getAsJsonObject());
			msgArrList.add(msg);
			Log.i("MESSAGE ADD",msg.getMessage());
		}
		Convo convo = new Convo(convoObj.get("_id").toString(),convoObj.get("_rev").toString(),convoObj.get("user1").toString(),convoObj.get("user2").toString(),msgArrList);
		return convo;
	}
	
	public Message getMessageFromJson(JsonObject msgObj){
		Message msg = new Message(msgObj.get("to").toString(),msgObj.get("from").toString(),msgObj.get("timestamp").toString(),msgObj.get("message").toString());
		return msg;
	} 
	//helper method to get the JSON object
    public String getJSON(String address){
    	
    	StringBuilder builder = new StringBuilder();
    	HttpClient client = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(address);
    	try{
    		HttpResponse response = client.execute(httpGet);
    		StatusLine statusLine = response.getStatusLine();
    		int statusCode = statusLine.getStatusCode();
    		if(statusCode == 200){
    			HttpEntity entity = response.getEntity();
    			InputStream content = entity.getContent();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    			String line;
    			while((line = reader.readLine()) != null){
    				builder.append(line);
    			}
    		} else {
    			Log.e("ShyHi","Failed to get JSON object");
    		}
    	}catch(ClientProtocolException e){
    		e.printStackTrace();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    	return builder.toString();
    }
    
  //helper method to PUT the JSON object
    public String putJSON(String url, JsonObject jsonObject) throws Exception{ 
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response;
            HttpPut put=new HttpPut();
            HttpEntity httpEntity;
            StringEntity stringEntity=new StringEntity(jsonObject.toString());
			Log.i("convoStr",jsonObject.toString());
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpEntity=stringEntity;
            put.setEntity(httpEntity);
            put.setURI(new URI(url));
            put.setHeader("Content-type", "application/json");
            response=httpClient.execute(put);
            return parseHttpResponse(response);
        }
    public  String parseHttpResponse(HttpResponse response) throws Exception {
        String jsonString="";
        int status = response.getStatusLine().getStatusCode();
        Log.i("Status",Integer.toString(status));
        if (status == 200) {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = bReader.readLine()) != null) {
                sb.append(line + NL);
            }
            jsonString = sb.toString();
            bReader.close();
        } 
        Log.i("HTTP Stuff",jsonString);
        return jsonString;
         
    }
	//helper AsyncTask class
	private class fetchJSON extends AsyncTask<String, Integer, String> {
        @Override
		protected String doInBackground(String... id) {
        	String convoJSON = getJSON(id[0]);
            return convoJSON;
        } 
        @Override
        protected void onPostExecute(String result) {
        	
        }
    }
	
}
