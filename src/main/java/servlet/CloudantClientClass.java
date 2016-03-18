import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.cloudfoundry.runtime.env.CloudEnvironment;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;



public class CloudantClientClass{

  private String urlStr;
  private String username;
  private String password;

  public CloudantClientClass(){

    try{
      setCredentials();
    }catch(Exception e){
      e.printStackTrace();
    }

  }

  public int addEntry(String jsonString) throws Exception, ParseException{

    JSONParser parser = new JSONParser();
    int result = 0;

    try{
      //create Cloudant client connection
      CloudantClient client = getClientConn();

      //get database. it will be autonamitcally created if not existing yet
      Database db = client.database("books", true);

      Object obj = parser.parse(jsonString);

      //check if json is an array or not
      if(obj.getClass().getName().matches(".*[JSONArray]")){

        JSONArray objArr = (JSONArray) obj;

        List<Object> entryObj = new ArrayList<Object>();

        for(int i=0; i < objArr.size();i++){
        
          entryObj.add(objArr.get(i));

        }

        //perform bulk insert for array of documents
        List<Response> response = db.bulk(entryObj);

        result = response.get(0).getStatusCode();


      }else{

        Response rs = db.save(obj);

        result = rs.getStatusCode();

      }


    }catch(ParseException pe){

      pe.printStackTrace();

    }catch(Exception e){

      e.printStackTrace();

    }

    return result;

  }

  public String getAll() throws Exception{

    String cred = username+":"+password;

    Base64.Encoder enc = Base64.getEncoder();

    //encode username:password string for authentication
    String encodedCred = new String(enc.encode(cred.getBytes()));
    String authorization = "Basic "+encodedCred;

    //accessing all the documents in the books database
    URL obj = new URL("https://"+urlStr+"/books/_all_docs?include_docs=true");
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    //create a GET request with Authorization header
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", authorization);

    int responseCode = con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

    String inputLine;
    StringBuffer response = new StringBuffer();

    //getting the response from the server in json String format
    while((inputLine = in.readLine()) != null){
      response.append(inputLine);
    }

    in.close();

    return response.toString();

  }

  public void setCredentials() throws Exception{

    CloudEnvironment environment = new CloudEnvironment();
    if ( environment.getServiceDataByLabels("cloudantNoSQLDB").size() == 0 ) {
      throw new Exception( "No Cloudant service is bound to this app!!" );
    } 

    Map credential = (Map)((Map)environment.getServiceDataByLabels("cloudantNoSQLDB").get(0)).get( "credentials" );
    
    this.urlStr = (String)credential.get("host");
    this.username = (String)credential.get("username");
    this.password = (String)credential.get("password");

  }


  protected CloudantClient getClientConn() throws Exception {

    CloudEnvironment environment = new CloudEnvironment();
    if ( environment.getServiceDataByLabels("cloudantNoSQLDB").size() == 0 ) {
      throw new Exception( "No Cloudant service is bound to this app!!" );
    } 

    Map credential = (Map)((Map)environment.getServiceDataByLabels("cloudantNoSQLDB").get(0)).get( "credentials" );

    CloudantClient client = (CloudantClient) ClientBuilder.account((String)credential.get("username"))
                                         .username((String)credential.get("username"))
                                         .password((String)credential.get("password"))
                                         .build();

    return client;
  }

}