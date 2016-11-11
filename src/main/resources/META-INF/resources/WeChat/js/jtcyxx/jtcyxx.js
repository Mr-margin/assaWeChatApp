var xb;//性别
var zjhm; //证件号码
var mz; //民族
var zzmm;//政治面貌
var whcd; //文化程度
var zxs; //在校生情况
var jkzk; //健康状况
var ldjn; //劳动技能
var wgqk; //务工情况
var sj;//务工时间
var junr;//现役军人
var baoxian;//医疗保险
var v6;//贫困户姓名
var v10;//与户主关系
var pic_path;//图片的地址
var type;//4、户主5、家庭成员
$(function (){
	var Request = new Object();
	Request = GetRequest();
	zjhm = Request['zjhm'];
	xb = Request['xb'];
	mz = Request['mz'];
	zzmm = Request['zzmm'];
	whcd = Request['whcd'];
	zxs = Request['zxs'];
	jkzk = Request['jkzk'];
	ldjn = Request['ldjn'];
	wgqk = Request['wgqk'];
	sj = Request['sj'];
	junr = Request['junr'];
	baoxian = Request['baoxian'];
	v6 = Request['v6'];
	v10 = Request['v10'];
	pic_path = Request['pic_path'];
	sid = Request['sid'];
	poor_id = Request['poor_id'];
	if(v10=='户主'){
		type = '4';
	}else{
		type = '5';
	}
	jtcyxx();
	
})
var qianming ;//签名
var c_time;//时间戳
var sj_num ;//随机数
var token ;//token
//获取签名
function qm(){
	$.ajax({  		       
	    url: '/assa/getQianming.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {url : window.location.href},
	    success: function (data) {
	    	c_time = data[0].time;
	    	sj_num = data[0].num;
	    	qianming = data[0].val;
	    	token = data[0].token;
	    },
	    error: function (data) {
	    	
	    }  
	})
}
//家庭成员信息
function jtcyxx(){
	
	var title_html = '<div>';
	if (pic_path == "" || pic_path == null || pic_path == undefined){
		title_html += '<img src="img/no-person.png" style="width:130px;height:140px;padding-left:5px;padding-top:10px;border-radius:10%;" onclick="add_touxiang()">'+
						'<span style="padding-left: 17px;padding-top:20px;position: absolute;">'+v6+'（'+v10+'）</span><div style="padding-left: 155px;margin-top: -100px;color: #666666; font-size:15px;"><p>性别：'+xb+'</p><p>民族：'+mz+'</p><p>'+zjhm+'</p></div>';
	}else{
		title_html += '<img src="'+pic_path+'" style="width:130px;height:140px;padding-left:5px;padding-top:10px;border-radius:10%;" onclick="add_touxiang()">'+
						'<span style="padding-left: 17px;padding-top:20px;position: absolute;">'+v6+'（'+v10+'）</span><div style="padding-left: 155px;margin-top: -100px;color: #666666; font-size:15px;"><p>性别：'+xb+'</p><p>民族：'+mz+'</p><p>'+zjhm+'</p></div>';
	}
	title_html += '</div>';
	$("#touxiang").html(title_html);
	var html = '<ul class="dorm-book mt3">'+
			'<li class="basic-tit">'+
			  '  成员基本信息'+
			'</li>'+
//			'<li>'+
//				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
//					'<img src="img/gender.png">'+
//				'</span>'+
//			   ' <span class="stu-name">性别</span></span>'+
//		       ' <span class="info-middle">'+xb+'</span>  '+
//			'</li>'+
//			'<li>'+
//				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
//					'<img src="img/pop.png">'+
//				'</span>'+
//			   ' <span class="stu-name">证件号码</span></span>'+
//		       ' <span class="info-middle">'+zjhm+'</span>  '+
//			'</li>'+
//			'<li>'+
//				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
//				'	<img src="img/phone.png">'+
//				'</span>'+
//			   ' <span class="stu-name">民族</span></span>'+
//		        '<span class="info-middle">'+mz+'</span>  '+
//			'</li>'+
			'<li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'<img src="img/icon_bank.png">'+
				'</span>'+
			   ' <span class="stu-name">政治面貌</span></span>'+
		    '<span class="info-middle">'+zzmm+'</span>  '+
			'</li>'+
			'<li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'<img src="img/icon_account.png">'+
				'</span>'+
			   ' <span class="stu-name">文化程度</span></span>'+
		        '<span class="info-middle">'+whcd+'</span> '+ 
			'</li>'+
			'<li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'<img src="img/standard.png">'+
				'</span>'+
			   ' <span class="stu-name">在校生情况</span></span>'+
		       ' <span class="info-middle">'+zxs+'</span>  '+
			'</li>'+
			'<li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'<img src="img/property.png">'+
				'</span>'+
			   ' <span class="stu-name">健康状况</span></span>'+
		       ' <span class="info-middle">'+jkzk+'</span>  '+
			'</li>'+
			'<li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'<img src="img/icon_army.png">'+
				'</span>'+
			   ' <span class="stu-name">劳动技能</span></span>'+
		        '<span class="info-middle">'+ldjn+'</span>  '+
			'</li>'+
            
           ' <li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'<img src="img/icon_poorreason.png">'+
				'</span>'+
			    '<span class="stu-name">务工情况</span></span>'+
		        '<span class="info-middle">'+wgqk+'</span>  '+
			'</li>'+
            '<li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
					'<img src="img/reason.png">'+
				'</span>'+
			   ' <span class="stu-name">其务工时间（月）</span></span>'+
		        '<span class="info-middle">'+sj+'</span>  '+
			'</li>'+
          '  <li>'+
			'	<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
			'		<img src="img/before.png">'+
			'	</span>'+
			'    <span class="stu-name">是否现役军人</span></span>'+
		    '    <span class="info-middle">'+junr+'</span>  '+
			'</li>'+
             '<li>'+
				'<span class="book-tit"><span class="heade-img" style="height:15px; width:15px;">'+
				'	<img src="img/before.png">'+
				'</span>'+
			    '<span class="stu-name">是否参加重大医疗保险</span></span>'+
		      '  <span class="info-middle">'+baoxian+'</span>  '+
			'</li>'+
		'</ul>';
	
	$("#jtcyxx").html(html);
}
var photo = '';
//选择头像
function add_touxiang(){
	qm();
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			appId: 'wx4fa9e95d9af2477a', // 必填，公众号的唯一标识
			timestamp: c_time, // 必填，生成签名的时间戳
			nonceStr: sj_num, // 必填，生成签名的随机串
			signature: qianming,// 必填，签名，见附录1
			jsApiList: ['checkJsApi',
	        'onMenuShareTimeline',
	        'onMenuShareAppMessage',
	        'onMenuShareQQ',
	        'onMenuShareWeibo',
	        'hideMenuItems',
	        'showMenuItems',
	        'hideAllNonBaseMenuItem',
	        'showAllNonBaseMenuItem',
	        'translateVoice',
	        'startRecord',
	        'stopRecord',
	        'onRecordEnd',
	        'playVoice',
	        'pauseVoice',
	        'stopVoice',
	        'uploadVoice',
	        'downloadVoice',
	        'chooseImage',
	        'previewImage',
	        'uploadImage',
	        'downloadImage',
	        'getNetworkType',
	        'openLocation',
	        'getLocation',
	        'hideOptionMenu',
	        'showOptionMenu',
	        'closeWindow',
	        'scanQRCode',
	        'chooseWXPay',
	        'openProductSpecificView',
	        'addCard',
	        'chooseCard',
	        'openCard'] // 必填，需要
		
			});
	wx.ready(function(){
	    // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
		 wx.chooseImage({
			    count: 9, // 默认9
			    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
			    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
			    success: function (res) {
			    	localIds = res.localIds; //返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
			    	$("#jt_tx").html('<img src="'+localIds+'">')
		        	wx.uploadImage({
			    	    localId: localIds[0], // 需要上传的图片的本地ID，由chooseImage接口获得
			    	    isShowProgressTips: 0, // 默认为1，显示进度提示
			    	    success: function (res) {
			    	        var serverId = res.serverId; // 返回图片的服务器端ID
			    	        photo = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+token+"&media_id="+serverId;
			    	        add_jtcy();
			    	        
			    	    }
			    	});
			    }
		 	});
		});
	wx.error(function(res){
		
	});
}
//上传头像到本地
function add_jtcy(){
	var html = '';
	$.ajax({  		       
	    url: '/assa/getAdd_jttx.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {poor_id:poor_id,photo:photo,type:type},
	    success: function (data) {
	    	if(data == '5'){
	    		alert('上传成功');
	    	}
	    	
	    },
	    error: function (data) { 
	    	
	    }  
	});
}