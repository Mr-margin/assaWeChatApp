package com.gistone.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gistone.MyBatis.config.GetBySqlMapper;
import com.gistone.util.Tool;

@RestController
@RequestMapping
public class PassWordController{

	@Autowired
	private GetBySqlMapper getBySqlMapper;

	/**
	 * @method 更新密码
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author 张晓翔
	 * @date 2016年8月4日下午4:58:14
	 */
	@RequestMapping("upPassword.do")
	public void upPassword(HttpServletRequest request,HttpServletResponse response) throws Exception{

		String id = request.getParameter("pkid");//获取用户ID
		String password = request.getParameter("password");//获取密码
		password = Tool.md5(password);
		String people_sql = "update sys_user set col_password='"+password+"' where pkid="+id;
		try{
			this.getBySqlMapper.update(people_sql);
			response.getWriter().write("1");
		}catch (Exception e){
			response.getWriter().write("0");
		}
	}

	/**
	 * @method 验证修改密码时输入是否和原密码相同
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author 张晓翔
	 * @date 2016年8月4日下午4:58:28
	 */
	@RequestMapping("o_password.do")
	public void o_password(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String password1 = request.getParameter("val");
		String id = request.getParameter("pkid");

		String sql = "SELECT count(*) FROM sys_user WHERE pkid='" + id + "' and col_password = '" + Tool.md5(password1) + "'";
		int resultSize = getBySqlMapper.findrows(sql);
		if (resultSize == 0){
			response.getWriter().print("0");
		}else{
			response.getWriter().print("1");
		}
	}
}
