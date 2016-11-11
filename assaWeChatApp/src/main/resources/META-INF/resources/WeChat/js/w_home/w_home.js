$(function(){
	var Request = new Object();
	Request = GetRequest();
	sid = Request['sid'];
	pkid = Request['pkid'];
	float(sid,pkid);
	document.getElementById("shouye").setAttribute("class","page-item active");
	menu_html();
})
//页面动态加载
function menu_html(){
	var html = 	'<ul class="clearfix" >'+
	'<li>'+
		'<a href="xiw.html" style="background-color:#008a9d;">'+
			'<img src="img/a1.png">'+
			'<p class="menu-txt">资讯头条</p>'+
		'</a>'+
	'</li>'+
	'<li>'+
		'<a href="bfdx.html?sid='+sid+'&pkid='+pkid+'" style="background-color:#20bcd2;">'+
			'<img src="img/a2.png">'+
			'<p class="menu-txt">帮扶对象</p>'+
		'</a>'+
	'</li>'+
	'<li>'+
		'<a href="tjzf.html?sid='+sid+'&pkid='+pkid+'" style="background-color:#00e1fa;">'+
			'<img src="img/a3.png">'+
			'<p class="menu-txt">写日记</p>'+
		'</a>'+
	'</li>'+
	'<li>'+
		'<a href="sm.html?sid='+sid+'&pkid='+pkid+'" style="background-color:#e3c97e;">'+
			'<img src="img/a4.png">'+
			'<p class="menu-txt">扫一扫</p>'+
		'</a>'+
	'</li>'+
'</ul>'+
'<ul class="clearfix">'+
	'<li>'+
		'<a href="bfrj.html?sid='+sid+'&pkid='+pkid+'" style="background-color:#00363d;">'+
			'<img src="img/a5.png">'+
			'<p class="menu-txt">我的日记</p>'+
		'</a>'+
	'</li>'+
	'<li>'+
		'<a href="notice.html" style="background-color:#004b51;">'+
			'<img src="img/a6.png">'+
			'<p class="menu-txt">帮助指南</p></a>'+
	'</li>'+
	
'</ul>';
	$("#menu").html(html);
}