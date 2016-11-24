package com.gistone;

import java.io.IOException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		System.out.println("每分钟执行一次。开始……");
		
		System.out.println("每分钟执行一次。结束。");
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
}
