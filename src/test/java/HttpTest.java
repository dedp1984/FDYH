import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;




public class HttpTest
{

	public static void main(String[] args) throws ClientProtocolException, IOException
	{
		// TODO Auto-generated method stub
		HttpClient httpClient=new DefaultHttpClient();
		HttpPost   httpPost=new HttpPost("http://112.74.89.134:8080/fengic/userlogin/login");
		JSONObject jsonHeader=new JSONObject();
		jsonHeader.put("service", "100080");
		jsonHeader.put("inputname", "aaa");
		jsonHeader.put("sign", "DDFDF");
		JSONObject jsonBody=new JSONObject();
		jsonBody.put("token", "100080");
		jsonBody.put("inputname", "aaaa");
		jsonBody.put("passwd", "DDFDF");
		jsonBody.put("loginmac", "DDFDF");
		JSONObject json=new JSONObject();
		json.put("reqheader", jsonHeader);
		json.put("reqbody", jsonBody);
		System.out.println(json.toJSONString());
		StringEntity s = new StringEntity(json.toJSONString());
		//s.setContentEncoding("UTF-8");
		//s.setContentType("application/json");
		s.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, Consts.UTF_8.toString()));
        s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "text/html;charset=UTF-8"));
        System.out.println(EntityUtils.toString(s));
		httpPost.setEntity(s);
		HttpResponse response=httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		String entityStr=EntityUtils.toString(entity);
		System.out.println(entityStr);
		System.out.println("Login form get: " + response.getStatusLine());
        if (entity != null) 
        {
            entity.consumeContent();
        }
		
//		
//		String tmp="{\"errors\":{\"password\":\"√‹¬Î¥ÌŒÛ\"},\"rtnval\":{},\"success\":true}";
//		JSONObject jsonObject=JSON.parseObject(tmp);
//        System.out.println(jsonObject.getBooleanValue("success"));
//        System.out.println(jsonObject.getJSONObject("errors").getString("password"));
//		Runnable myRun=new MyRun();
//		Thread t1=new Thread(myRun,"t1");
//		t1.start();
//		Thread t2=new Thread(myRun,"t2");
//		t2.start();
	}
	
	

}
