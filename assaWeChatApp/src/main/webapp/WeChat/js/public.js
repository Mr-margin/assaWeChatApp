var name = '';//帮扶人
var phone = '';//帮扶人电话
function GetRequest() { //截取URL的方法
	   var url = location.search; //获取url中"?"符后的字串
	   var theRequest = new Object();
	   if (url.indexOf("?") != -1) {
	      var str = url.substr(1);
	      strs = str.split("&");
	      for(var i = 0; i < strs.length; i ++) {
	    	  theRequest[strs[i].split("=")[0]]=decodeURI(strs[i].split("=")[1]); 
	      }
	   }
	   return theRequest;
	}

function float(phone,name){
	
	var html = '<ul class="footer-page clearfix">'+
			'<li id="shouye" class="page-item"><a href="w_home.html?phone='+phone+'&name='+name+'" class="active"> <i '+
					'class="iconfont icon-index"></i>'+
				'	<p>首页</p>'+
			'</a></li> '+
			'<li id="geren" class="page-item"><a href="grxx.html?name='+phone+'&name='+name+'"> <i '+
				'	class="iconfont icon-person1"></i>'+
					'<p>个人中心</p>'+
			'</a></li>'+
		'</ul>';
	
	
	$("#footer").html(html)
}