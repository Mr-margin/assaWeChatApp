var page_option = 0;
var ctype =1;//查询级别
var xzqh = "0";//行政区划名称
var code = "150000000000";//行政区划编码

seticon(page_option);


function s_diary_sta() {
    $(window.parent.document).find("#if_content").attr("src", "diary_statistics.html?cType=" + ctype + "&xzqh=" + encodeURI(xzqh) + "&code=" + code);
    /* $(window.parent.document).find("#if_content").attr("src","diary_statistics.html?cType="+1);*/
    page_option = 0;
    seticon(page_option);
}

function s_diary_bro() {
    $(window.parent.document).find("#if_content").attr("src", "diary_browse.html?cType=" + ctype + "&xzqh=" + encodeURI(xzqh) + "&code=" + code);
    page_option = 2;
    seticon(page_option);
}

function s_poverty_situation() {
    $(window.parent.document).find("#if_content").attr("src", "poverty_situation.html?cType=" + ctype + "&xzqh=" + encodeURI(xzqh) + "&code=" + code);

    page_option = 1;
    seticon(page_option);
}

function close_bft() {
    var mdialog = '<div id="close_dialog" >' +
        '<div class="weui-mask"></div>' +
        '<div class="weui-dialog">' +
        '<div class="weui-dialog__hd"><strong class="weui-dialog__title">关闭平台</strong></div>' +
        '<div class="weui-dialog__bd">您确定要退出平台吗？</div>' +
        '<div class="weui-dialog__ft">' +
        '<a id="close_cancel" class="weui-dialog__btn weui-dialog__btn_default">取消</a>' +
        '<a id="close_primary" class="weui-dialog__btn weui-dialog__btn_primary">确定</a>' +
        '</div>' +
        '</div>' +
        '</div>';

    $('body').append(mdialog);

    $('#close_cancel').click(function () {
        $('#close_dialog').remove();
    });
    $('#close_primary').click(function () {
        $('#close_dialog').remove();
        WeixinJSBridge.call('closeWindow');
    });
}


function seticon(option){
    if (option == 0){
        $("#bftjic").attr("src","images/bftj2.png");
        $("#fpxzic").attr("src","images/fpxz.png");
        $("#zfrjic").attr("src","images/zfrj.png");
        $("#diary_sta").addClass("weui-bar__item_on");
        $("#diary_bro").removeClass("weui-bar__item_on");
        $("#poverty_situation").removeClass("weui-bar__item_on");
    }else if(option == 1){
        $("#bftjic").attr("src","images/bftj.png");
        $("#fpxzic").attr("src","images/fpxz2.png");
        $("#zfrjic").attr("src","images/zfrj.png");
        $("#diary_sta").removeClass("weui-bar__item_on");
        $("#diary_bro").removeClass("weui-bar__item_on");
        $("#poverty_situation").addClass("weui-bar__item_on");
    }else {
        $("#diary_sta").removeClass("weui-bar__item_on");
        $("#diary_bro").addClass("weui-bar__item_on");
        $("#poverty_situation").removeClass("weui-bar__item_on");
        $("#bftjic").attr("src","images/bftj.png");
        $("#fpxzic").attr("src","images/fpxz.png");
        $("#zfrjic").attr("src","images/zfrj2.png");
    }
}