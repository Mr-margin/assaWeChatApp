var dz;//地址
var renshu;//户籍人数
var phone;//联系电话
var khyh;//开户银行
var yhzh;//银行账号
var sbbz;//识别标准
var shuxing;//属性
var jls;//军烈属
var zpyy;//主要致贫原因
var qtzp;//其他致贫原因
var url;//照片的url
var v6;//户主姓名
var poor_id;//贫困户的id
var bottom;//0、家庭成员 走访记录 1、反之
var pkhbh;//贫困户编号
var zjhm;//证件号码
$(function () {
	var Request = new Object();
	Request = GetRequest();
	dz =  Request['dz'];
	renshu =  Request['renshu'];
	phone =  Request['phone'];
	khyh =  Request['khyh'];
	yhzh =  Request['yhzh'];
	sbbz =  Request['sbbz'];
	shuxing =  Request['shuxing'];
	jls =  Request['jls'];
	zpyy =  Request['zpyy'];
	qtzp =  Request['qtzp'];
	url = Request['url'];
	v6 = Request['v6'];
	sid = Request['sid'];
	poor_id = Request['poor_id'];
	bottom = Request['bottom'];
	pkhbh = Request['pkhbh'];
	zjhm = Request['zjhm'];
	poor_xinxi();
})
//贫困户的详细信息
function poor_xinxi(){
	var title_html = '<div>';
	if (url == "" || url == null || url == undefined){
		title_html += '<img src="img/no-person.png" style="width:130px;height:140px;padding-left:10px;padding-top:10px;border-radius:10%"><span style="padding-left: 70px;padding-top:30px;position: absolute;">'+v6+'</span>';
		title_html += '<div style="padding-left: 160px;margin-top: -50px;"><a href="jtcy.html?pkhbh='+pkhbh+'" style="padding: 1% 8%;font-size: .48rem;background: #00A7EE;border-radius: 4px;margin-right: 2%;color: #fff;width: 100px;">家庭成员</a>';
		title_html += '<a href="bfrj.html?household_name='+v6+'&zjhm='+zjhm+'" style="padding: 1% 8%;font-size: .48rem;background: #00A7EE;border-radius: 4px;margin-right: 2%;color: #fff;width: 100px;">走访记录</a><div>';
	}else{
		title_html += '<img src="'+url+'" style="width:130px;height:140px;padding-left:10px;padding-top:10px;border-radius:10%"><span style="padding-left: 70px;padding-top:30px;position: absolute;">'+v6+'</span>';
		title_html += '<div style="padding-left: 160px;margin-top: -50px;"><a href="jtcy.html?pkhbh='+pkhbh+'" style="padding: 1% 8%;font-size: .48rem;background: #00A7EE;border-radius: 4px;margin-right: 2%;color: #fff;width: 100px;">家庭成员</a>';
		title_html += '<a href="bfrj.html?household_name='+v6+'&zjhm='+zjhm+'" style="padding: 1% 8%;font-size: .48rem;background: #00A7EE;border-radius: 4px;margin-right: 2%;color: #fff;width: 100px;">走访记录</a><div>';
	}
	title_html += '</div>';
	$("#touxiang").html(title_html);
	var html = 	'<ul class="dorm-book mt3">'+
					'<li class="basic-tit">'+
				    '帮扶对象基本信息'+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
						'<img src="img/address.png">'+
					'</span>'+
				   ' <span class="stu-name">家庭住址</span></span>'+
				   ' <span class="info-middle">'+dz+'</span>  '+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
						'<img src="img/pop.png">'+
					'</span>'+
				    '<span class="stu-name">户籍人数</span></span>'+
				    '<span class="info-middle">'+renshu+'</span>  '+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
						'<img src="img/phone.png">'+
					'</span>'+
				   ' <span class="stu-name">联系电话</span></span>'+
				    '<span class="info-middle">'+phone+'</span>  '+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
						'<img src="img/icon_bank.png">'+
					'</span>'+
				   ' <span class="stu-name">开户银行</span></span>'+
				'<span class="info-middle">'+khyh+'</span>  '+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'	<img src="img/icon_account.png">'+
					'</span>'+
				   ' <span class="stu-name">银行账号</span></span>'+
				   '<span class="info-middle">'+yhzh+'</span>  '+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
						'<img src="img/standard.png">'+
					'</span>'+
				   ' <span class="stu-name">识别标准</span></span>'+
				    '<span class="info-middle">'+sbbz_sz[sbbz-1]+'</span>  '+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'	<img src="img/property.png">'+
					'</span>'+
				   ' <span class="stu-name">贫困户属性</span></span>'+
				   ' <span class="info-middle">'+pkhsx_sz[shuxing-1]+'</span>  '+
				'</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'	<img src="img/icon_army.png">'+
					'</span>'+
				  '  <span class="stu-name">是否军烈属</span></span>'+
				  '  <span class="info-middle">'+jls_sz[jls]+'</span>  '+
				'</li>'+
				
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
						'<img src="img/icon_poorreason.png">'+
					'</span>'+
				    '<span class="stu-name">主要致贫原因</span></span>';
					if ( zpyy == 99 ) {
						html +=  ' <span class="info-middle">其他</span>';
					} else if (zpyy == "" || zpyy == undefined  ) {
						html +=  ' <span class="info-middle"></span>';
					}else {
						html +=  ' <span class="info-middle">'+zyzy_sz[zpyy-1]+'</span>';
					}
				html +='</li>'+
				'<li>'+
					'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'	<img src="img/reason.png">'+
					'</span>'+
				   ' <span class="stu-name">其他致贫原因</span></span>';
				if ( qtzp == 99 ) {
					html+= '  <span class="info-middle">其他</span>  ';
				} else if (qtzp=="" || qtzp == undefined ){
					html+= '  <span class="info-middle"></span>  ';
				}
				else {
					html+= '  <span class="info-middle">'+qtzy_sz[qtzp-1]+'</span>  ';
				}
				html += '</li>'+
				'</ul>';
	$("#poor_xinxi").html(html);
	
}