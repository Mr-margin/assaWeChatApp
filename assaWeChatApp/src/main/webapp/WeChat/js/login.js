window.onload=function(){
    var aInput=document.getElementsByTagName('input');
    var oUser=aInput[0];
	if (localStorage.bftusername!=null && localStorage.bftusername != undefined){
		aInput[0].value = localStorage.bftusername;
	}
    var oPwd=aInput[1];
    var aI=document.getElementsByTagName('i')[0];
}

$(function(){
//	var userAgentInfo = navigator.userAgent;
//    var Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
//    var flag = true;
//    for (var v = 0; v < Agents.length; v++) {
//        if (userAgentInfo.indexOf(Agents[v]) > 0) {
//            flag = false;
//            break;
//        }
//    }
//    if(flag == true){
//        alert("请使用手机端登录");
//        $("#login").hide();
//    }else{
    	 $("#login").show();
//    }
	
})


//手机登录
function login(){

	if ($("#username").val().length != 11){
		alert("请输入正确的11位手机号码！");
		return;
	}
	$.ajax({  		       
	    url: '/assaWeChatApp/getAnLoginController.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {phone:$("#username").val(),password:$("#password").val()},
	    success: function (data) {
	    	if(data.message == '密码错误'){
	    		$("#tishi").html("密码错误");
	    	}else if (data.message == '用户不存在') {
	    		$("#tishi").html("账号不存在");
	    	}else if(data.message == '登录成功'){
				localStorage.bftusername = $("#username").val();
	    		window.location.href = "w_home.html?phone="+data.data.phone+"&name="+data.data.name;
	    	}else if(data.message == '0'){
				localStorage.bftusername = $("#username").val();
				window.location.href = "http://www.gistone.cn/assaWeChatApp/BFTLeader/index.html";
			}
	    },
	    error: function (ret) { 
	    	alert('登录失败')
	    }  
	})
}
function verify_phone(obj){
	if(obj.value.length==1){
		obj.value=obj.value.replace(/[^1-9]/g,'');
	}else{
		obj.value=obj.value.replace(/\D/g,'');
	}
}