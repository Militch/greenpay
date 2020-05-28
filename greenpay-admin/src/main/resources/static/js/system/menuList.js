/**
 * 权限列表
 */
let $ = layui.jquery,
    layer = layui.layer
    ,form = layui.form;
!(function () {

    var table = layui.table
        ,form = layui.form,
        layer = layui.layer;
    //监听工具条
    table.on('tool(userTable)', function(obj){
        var data = obj.data;
        if(obj.event === 'del'){
            delUser(obj,layer,data);
        } else if(obj.event === 'edit'){
            //编辑

        } else if(obj.event === 'recover'){
            //恢复
            recoverUser(data,data.id);
        }
    });
    //监听提交
    form.on('submit(userSubmit)', function(data){
        formSubmit(data);
        return false;
    });
    //操作
    layui.use('form', function(){
        var form = layui.form;
        //监听提交
        form.on('submit(permSubmit)', function(data){
            // $("#type").val($("input[name='style']:checked").val());
            let style = data.field.style;
            switch (style) {
                case "0":
                    $.ajax({
                        type: "PUT",
                        data: $("#permForm").serialize(),
                        url: "/admin/api/v1/system/menus",
                        success: function (data) {
                            if (data == "ok") {
                                layer.alert("操作成功",function(){
                                    layer.closeAll();
                                });
                            } else {
                                layer.alert(data);
                            }
                        },
                        error: function (data) {
                            layer.alert("操作请求错误，请您稍后再试");
                        }
                    });
                    break;
                case "1":
                    $.ajax({
                        type: "POST",
                        data: $("#permForm").serialize(),
                        url: "/admin/api/v1/system/menus",
                        success: function (data) {
                            if (data == "ok") {
                                layer.alert("操作成功",function(){
                                    layer.closeAll();
                                });
                            } else {
                                layer.alert(data);
                            }
                        },
                        error: function (data) {
                            layer.alert("操作请求错误，请您稍后再试");
                        }
                    });
                    break;
                default:
                    break;
            }

            return false;
        });
        form.render();
    });




}());
function edit(id,style){
    if(null!=id){
        $("#style").val(style);
        $("#id").val(id);
        $.get("/admin/api/v1/system/menus/"+id,function(data) {
            console.log(data)
            // console.log(data);
            if(null!=data){
                $("input[name='title']").val(data.title);
                $("input[name='mark']").val(data.mark);
                $("input[name='path']").val(data.path);
                $("input[name='sorts']").val(data.sorts);
                $("input[name='icon']").val(data.icon);
                $("textarea[name='extra']").text(data.extra);
                $("input[type='radio'][value='1']").attr("checked", data.type == 1 ? true : false);
                $("input[type='radio'][value='2']").attr("checked", data.type == 2 ? true : false);
                form.render()
                $("#parentId").val(data.parentId);

                // var sex = 2;
                // $(":radio[name='rbsex'][value='" + sex + "']").prop("checked", "checked");
                // data.type==0?$("input[name='style']").val(1).checked:$("input[name='style']").val(2).checked;
                // console.log($("input[name='rbsex']:checked").val());
                layer.open({
                    type:1,
                    title: "更新权限",
                    fixed:false,
                    resize :false,
                    shadeClose: true,
                    area: ['500px', '580px'],
                    content:$('#updatePerm'),
                    end:function(){
                        location.reload();
                    }
                });

            }else{
                layer.alert("获取权限数据出错，请您稍后再试");
            }
        });
    }
}
//开通权限
function addPerm(parentId,dataType,flag){
    if(null!=parentId){
        //flag[0:开通权限；1：新增子节点权限]
        //style[0:编辑；1：新增]
        if(flag==0){
            $("#style").val(1);
            $("#parentId").val(0);
        }else if(flag==1){
            //添加子节点
            $("#style").val(1);
            //设置父id
            $("#parentId").val(parentId);
        }
        if(dataType==2){
            layer.alert("按钮类型不能添加子节点");
            return false;
            // $('#radio').css('display','none')
        }else{
            $('#radio').css('display','block')

        }
        layer.open({
            type:1,
            title: "添加权限",
            fixed:false,
            resize :false,
            shadeClose: true,
            area: ['500px', '580px'],
            content:$('#updatePerm'),  //页面自定义的div，样式自定义
            end:function(){
                location.reload();
            }
        });
    }
}

function del(menuId,name){
    // console.log("===删除id："+id);
    if(null!=menuId){
        layer.confirm('您确定要删除'+name+'权限吗？', {
            btn: ['确认','返回'] //按钮
        }, function(){
            $.ajax({
                type: "DELETE",
                data: {'menuId': menuId},
                url: "/admin/api/v1/system/menus/del",
                success: function (data) {
                    if (data) {
                        layer.alert("操作成功",function(){
                            layer.closeAll();
                            location.reload();//自定义
                        });
                    } else {
                        layer.alert(data);
                    }
                },
                error: function (data) {
                    layer.alert(data.responseJSON.message);
                }
            });
        });
        //     $.del("/admin/api/v1/system/menus/del",{"id":id},function(data){
        //         if(data=="ok"){
        //             //回调弹框
        //             layer.alert("删除成功！",function(){
        //                 layer.closeAll();
        //                 //加载load方法
        //                 location.reload();;//自定义
        //             });
        //         }else{
        //             layer.alert(data);//弹出错误提示
        //         }
        //     });
        // }, function(){
        //     layer.closeAll();
        // });
    }

}

//关闭弹框
function close(){
    layer.closeAll();
}