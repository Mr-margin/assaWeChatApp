$(function () {
	
	guobiao();//所有业务数据，默认国标
});

var com_level=1;//用户层级
var code;//用户code
var gors;//国标或者市标


function guobiao(){//点击国标
	alert(1)
	$.ajax({
		url:"getReceiveParameter.do",
	    type: "POST",
	    async:false,
	    dataType: "json",
	    data:"",
	    success:function(data){
	    	gors=data.stardand;
	    	code=data.code;
	    },
	    error :function(data){
//	    	alert("mieyou");
	    }
	})
	index_map(code,gors);//加载工作首页地图
}


//加载工作首页地图
function index_map(code,gors){
	if(code=="150622001000"||code=="150622002000"||code=="150622003000"||code=="150622004000"){
		$("#mapChart").html('<div style="padding-left: 150px;padding-top: 150px;"><h3>暂无 '+jsondata.company.com_name+'-'+gors+' 地图</h3></div>');
	}else{
		var mapdatajson;//定义地图JSON
		var map_name;//定义地图名称
		if(code=="150600000"){//如果层级为1，那么加载鄂尔多斯市地图
			mapdatajson='../mapData/eerduosi/ordos.json';//地图JSON
			map_name='ordos';//地图名称
		}else{//层级不为1，那么动态加载地图
			mapdatajson='../mapData/eerduosi/'+code+'.json';//地图JSON
			map_name=code;//地图名称
		}

		var myChart = echarts.init(document.getElementById('mapChart'));//声明id为mapChart的div为图形dom
		myChart.showLoading();//此方法是显示加载动画效果
		alert(mapdatajson)
		$.getJSON(mapdatajson, function (geoJson) {//获取已经定义好的json
		    myChart.hideLoading();//隐藏加载动画
		    echarts.registerMap(map_name, geoJson);//注册可用的地图，必须在包括 geo 组件或者 map 图表类型的时候才能使用
		    $.ajax({
		    	url:"getCharController.do",
		 	    type: "POST",
		 	    async:false,
		 	    dataType: "json",
		 	    data:{
		 	    	gors:gors,
		 	    	code:code,
		 	    },
		 	    success: function (data) {
		 	    	var option = {
		 					title: {//标题
		 						text : ''
		 					},
		 					tooltip : {//提示框组件相关的行为，必须引入提示框组件后才能使用。
		 						trigger: 'item'//数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用。
		 					},
		 					dataRange: {//感觉这个是和visualMap一样，对范围进行确定。
		 						show:true,//是否显示范围条
		 						right: '130',
//		 				        min: 0,
//		 				        max: 3100,
//		 				        color:["#BADB58"," #C9E47D","#D8EB9A","#E3F5B9","#F2FBDC"],
		 				        color:["#F40000","#F53D00","#F57B00","#F4B800","#FFFFBE"],
		 				        text:['高','低'],           // 文本，默认为数值文本
		 				        calculable : true//是否启用值域漫游，即是否有拖拽用的手柄，以及用手柄调整选中范围。
		 				    },
		 					series : [{
		 						name: '贫困户数(户)',//这里是鼠标放在上面，出现的提示。
		 						type: 'map',
		 						//roam:true,//是否开启鼠标缩放和平移漫游。默认不开启。如果只想要开启缩放或者平移，可以设置成 'scale' 或者 'move'。设置成 true 为都开启
		 						scaleLimit:{max:2, min:1},//滚轮缩放的极限控制，通过min, max最小和最大的缩放值，默认的缩放为1。
		 						mapType: map_name,
		 						selectedMode : 'single',//图例选择的模式，默认开启图例选择，可以设成 false 关闭。除此之外也可以设成 'single' 或者 'multiple' 使用单选或者多选模式。
		 						itemStyle:{//地图区域的多边形 图形样式，有 normal 和 emphasis 两个状态，normal 是图形正常的样式，emphasis 是图形高亮的样式，比如鼠标悬浮或者图例联动高亮的时候会使用 emphasis 作为图形的样式。
		 							normal:{label:{show:true}},
		 							emphasis:{label:{show:true}}
		 						},
		 						itemStyle: {
		 							normal: {
		 								label: {
		 									show: true,
		 									textStyle: {color: "black"}//地图上的字体
		 								},
		 								areaStyle : "#E0ECF6",
		 								borderColor: "#E0ECF6",
		 								borderWidth: "2.0",
		 								color: "#6CAFED"
		 							},
		 							emphasis: {
		 								label: {
		 									show: true,
		 									textStyle: {color: "black"}
		 								},
		 								borderColor: "#fff",
		 								borderWidth: "1.0",
		 								color: "#BADB58"
		 							}
		 						},
		 						data:[]
		 					}]
		 	        	};
		 	    	option.series[0].data = data;
		 	    	var min = 0,max = 0;
		 	    	$.each(data, function(i,item) {
		 	    		if(item.value>max){
		 	    			max = item.value;
		 	    		}
		 	    		if(item.value<min){
		 	    			min = item.value;
		 	    		}
		 	    	});
		 	    	option.dataRange.max = max;
		 	    	option.dataRange.min = min;
		 		    myChart.setOption(option);
		 	    },
		 	    error: function () { 
//		 	    	alert("错误")
//		 	    	toastr["warning"]("warning", "失败，检查数据后重试");
		 	    } 
			})
		    
		});
	}
	
}
