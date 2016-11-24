package com.gistone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan
@MapperScan("com.gistone.MyBatis.config")
public class WeChatApp extends SpringBootServletInitializer {
	public static String tokenn;
	public static String ticket;
	public static String f_noncestr;
	public static String f_timestamp;
	@RequestMapping(value="/")
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.sendRedirect("/index.html");
		return;
    }
	
	@RequestMapping(value="")
    public void home_1(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.sendRedirect("/index.html");
		return;
    }
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WeChatApp.class);
    }
	
	/**
	 * 定时任务
	 */
//	@Scheduled(cron="0 0/1 * * * ?") //每分钟执行一次
	@Scheduled(fixedRate=3600000)
	public void statusCheck() {
		
		String url1 = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx4fa9e95d9af2477a&secret=d0fa7c87870507490f4bedc671bcbc14";
		String json = loadJSON(url1);
		JSONObject  jasonObject = JSONObject.fromObject(json);
		Map map = (Map)jasonObject;
		String  str1 = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+map.get("access_token").toString()+"&type=jsapi";
		tokenn = map.get("access_token").toString();
		String token = loadJSON(str1); 
		JSONObject  ticket_obj = JSONObject.fromObject(token);
		Map ticket_map = (Map)ticket_obj;
		ticket = ticket_map.get("ticket").toString();
		f_noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串  
	    f_timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳 
//		System.out.println("每分钟执行一次。开始……");
//		
//		System.out.println("每分钟执行一次。结束。");
	}
	
	/**
	 * 文件上传临时路径
	 */
	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
	    factory.setLocation("/"); 
	    return factory.createMultipartConfig();
	}
	
    public static void main(String[] args) {
        SpringApplication.run(WeChatApp.class, args);
    }
	/**
	 * 请求参数
	 * @param url
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	 public static String loadJSON (String url) {
	        StringBuilder json = new StringBuilder();
	        try {
	            URL oracle = new URL(url);
	            URLConnection yc = oracle.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	            String inputLine = null;
	            while ( (inputLine = in.readLine()) != null) {
	                json.append(inputLine);
	            }
	            in.close();
	        } catch (MalformedURLException e) {
	        } catch (IOException e) {
	        }
	        return json.toString();
	    }
}
