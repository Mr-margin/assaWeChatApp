$(function(){
	var Request = new Object();
	Request = GetRequest();
	name = Request['name'];
	phone = Request['phone'];
})
//更改密碼
function chage_password(){
	if($("#new_password").val() != $("#new_password").val()){
		alert("密码与确认密码不一致");
		return;
	}
	$.ajax({  		       
	    url: '/assaWeChatApp/getUpdatePassword.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {name : name,phone:phone,old_password:$("#old_password").val(),new_password:$("#new_password").val()},
	    success: function (data) {
	    	if( data == "5"){
	    		alert("修改成功");
	    		$("#old_password").val("");
	    		$("#new_password").val("");
	    		$("#new_password1").val("");
	    	}else if ( data == "0" ){
	    		alert("原密码不正确");
	    	}
	    },
	    error: function (data) {
	    	
	    }  
	})
}