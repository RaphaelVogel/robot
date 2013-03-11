package core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Video extends HttpServlet{
	private static final long serialVersionUID = 5900899424591865115L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://192.168.178.17/video/mjpg.cgi?profileid=3");
        HttpResponse videoResponse = httpclient.execute(httpGet);
        
        for(Header header : videoResponse.getAllHeaders()){
        	response.setHeader(header.getName(), header.getValue());
        }
        
		if(videoResponse.getEntity() != null){
			IOUtils.copy(videoResponse.getEntity().getContent(), response.getOutputStream());
			EntityUtils.consume(videoResponse.getEntity());
		}
        
	}
}
