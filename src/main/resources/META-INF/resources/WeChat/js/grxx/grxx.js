
$(function(){
	var Request = new Object();
	Request = GetRequest();
	sid = Request['sid'];
	pkid = Request['pkid'];
	float(sid,pkid);
	document.getElementById("geren").setAttribute("class","page-item active");
	xinxi();
})

function xinxi(){
	
	var html = '<li class="ask-item">'+
			'<a href="#"> <i class="iconfont icon-data"></i> '+
			'<span class="ask-word">个人信息</span> '+
			'<span class="goin"><i class="iconfont icon-right"></i></span>'+
			'</a>'+
		'</li>'+
		'<li class="ask-item nobor">'+
			'<a href="xgmm.html?pkid='+pkid+'"> '+
				'<i class="iconfont icon-changpsw"></i> '+
				'<span class="ask-word">修改密码</span>'+
				'<span class="goin"><i class="iconfont icon-right"></i></span>'+
			'</a>'+
		'</li>';
//		'<li class="sign-out">'+
//			'<a href="w_login.html"> <i class="iconfont icon-logout"></i> <span>退出</span>'
//		'</li>';
	
	$("#user_xinxi").html(html);
	
}