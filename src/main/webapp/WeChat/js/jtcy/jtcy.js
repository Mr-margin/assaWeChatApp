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
				var jkzk = jkzk_sz[item.v15-1]==undefined?"未填写":jkzk_sz[item.v15-1];
				var ldl = ldjn_sz[item.v2-1]==undefined?"未填写":ldjn_sz[item.v2-1];
	    		html += '<ul class="answer-content">'+
						'<a href="jtcyxx.html?xb='+item.v7+'&zjhm='+item.v8+'&mz='+item.v11+'&whcd='+item.v12+''+
							'&zxs='+item.v13+'&jkzk='+item.v15+'&ldjn='+item.v15+'&wgqk='+item.v16+'&sj='+item.v17+'&junr='+item.v32+'&v6='+item.v6+'&v10='+item.v10+'&pic_path='+item.pic_path+'&pkrkbh='+item.v1+'">'+
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
											'<th scope="col" style="width:70%">';
											if ( item.v10 == 99 ) {
												html +=  '<h3 class="list-tit">'+item.v6+'（其他）</h3>';
											}else {
												html +=  '<h3 class="list-tit">'+item.v6+'（'+hzgx_sz[item.v10-1]+'）</h3>';
											}
											if ( item.v11 == 99 ) {
												html +=	'<p style="font-size:15px; color:#666666;">'+xb_sz[item.v7-1]+'&nbsp;&nbsp;其他</p>';
											} else {
												html +=	'<p style="font-size:15px; color:#666666;">'+xb_sz[item.v7-1]+'&nbsp;&nbsp;'+minzu_sz[item.v11-1]+'</p>';
											}
											html +=	'<p style="font-size:15px; color:#666666;">'+item.v8+'</p>'+
												'<p style="font-size:15px; color:#666666;">健康状况：'+jkzk+'</p>'+
												'<p style="font-size:15px; color:#666666;">劳动力：'+ldl+'</p>'+
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