var pkhbh ;

$(function () {
	var Request = new Object();
	Request = GetRequest();
	pkhbh = Request['pkhbh'];
	jtcy();
})
//家庭成员列表
function jtcy(){
	var html = '';
	$.ajax({  		       
	    url: '/assaWeChatApp/getSaveFamily.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {pkid:pkhbh},
	    success: function (data) {
	    	$.each(data.data,function(i,item){
	    		html += '<ul class="answer-content">'+
						'<a href="jtcyxx.html?xb='+item.v7+'&zjhm='+item.v8+'&mz='+item.v11+'&whcd='+item.v12+''+
							'&zxs='+item.v13+'&jkzk='+item.v15+'&ldjn='+item.v15+'&wgqk='+item.v16+'&sj='+item.v17+'&junr='+item.v32+'&v6='+item.v6+'&v10='+item.v10+'&pic_path='+item.pic_path+'">'+
							'<li class="answer-list">'+
								'<table width="100%" border="0" cellspacing="0" cellpadding="0">'+
									'<tbody>'+
										'<tr>'+
											'<th scope="col" style="width:30%">';
											if(item.pic_path == "" || item.pic_path == null || item.pic_path == undefined){
												html += '<img src="img/timg.jpg"';
											}else { 
												html += '<img src="'+item.pic_path+'"';
											}
											
											html +=	'style="width:120px; margin-right:20px;height:150px; border-radius:10%;"></th>'+
											'<th scope="col" style="width:70%">'+
												'<h3 class="list-tit">'+item.v6+'（'+item.v10+'）</h3>'+
												'<p style="font-size:15px; color:#666666;">'+item.v7+'&nbsp;&nbsp;'+item.v11+'</p>'+
												'<p style="font-size:15px; color:#666666;">'+item.v8+'</p>'+
												'<p style="font-size:15px; color:#666666;">健康状况：'+item.v15+'</p>'+
												'<p style="font-size:15px; color:#666666;">劳动力：'+item.v2+'</p>'+
											'</th>'+
										'</tr>'+
									'</tbody>'+
								'</table>'+
							'</li>'+
						'</a>'+
					'</ul>';
	    	})
	    	$("#jtcy").html(html);
	    },
	    error: function (data) { 
	    	
	    }  
	});
}