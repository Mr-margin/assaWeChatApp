var sid = '';//帮扶人id
var pkid = '';//用户表id
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

function float(sid,pkid){
	
	var html = '<ul class="footer-page clearfix">'+
			'<li id="shouye" class="page-item"><a href="w_home.html?sid='+sid+'&pkid='+pkid+'" class="active"> <i '+
					'class="iconfont icon-index"></i>'+
				'	<p>首页</p>'+
			'</a></li> '+
			'<li id="geren" class="page-item"><a href="grxx.html?sid='+sid+'&pkid='+pkid+'"> <i '+
				'	class="iconfont icon-person1"></i>'+
					'<p>个人中心</p>'+
			'</a></li>'+
		'</ul>';
	
	
	$("#footer").html(html)
}