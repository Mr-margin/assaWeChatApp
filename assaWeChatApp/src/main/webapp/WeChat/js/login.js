window.onload=function(){
    var aInput=document.getElementsByTagName('input');
    var oUser=aInput[0];
    var oPwd=aInput[1]
    var aI=document.getElementsByTagName('i')[0];
}

$(function(){
	var userAgentInfo = navigator.userAgent;
    var Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
            flag = false;
            break;
        }
    }
    if(flag == true){
        alert("请使用手机端登录");
        $("#login").hide();
    }else{
    	 $("#login").show();
    }
	
})
//手机登录
function login(){
	$.ajax({  		       
	    url: '/assa/getAnLoginController.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {phone:$("#username").val(),password:$("#password").val()},
	    success: function (data) {
	    	if(data.message == '0'){
	    		$("#tishi").html("密码错误");
	    	}else if (data.message == '1') {
	    		$("#tishi").html("账号尚未激活");
	    	}else if(data.message == '5'){
	    		window.location.href = "w_home.html?sid="+data.sid+"&pkid="+data.pkid;
	    	}
	    },
	    error: function (ret) { 
	    	alert('登录失败')
	    }  
	})
}
