/**
 * 权限列表
 */
let $ = layui.jquery


!(function() {
    let $ = layui.jquery;
    let table = layui.table
        ,form = layui.form
        ,layer = layui.layer;
    layer.config({
        extend: 'mycss/buttonstyle.css' //同样需要加载新皮肤
    });

        table.render({
            id: "roleLoad"
            ,elem: '#demo'
            ,url: '/api/v1/system/roles' //数据接口
            ,page: true //开启分页
            ,parseData: function(res){ //res 即为原始返回的数据
                return {
                    "code": 0,
                    "msg": res.message,
                    "count": res.total,
                    "data": res.records
                };
            }
            ,request: {
                pageName: 'current'
                ,limitName: 'size'
            }
            ,cols: [[ //表头
                {type:'checkbox'}//{type: 'checkbox', fixed: 'left'}
                ,{field: 'id', title: 'ID',width:'5%', unresize:true , sort: true}
                ,{field: 'name', title: '角色名称', width:'35%', unresize:true}
                ,{field: 'roleCode', title: '角色编码', width:'15%', unresize:true}
                ,{field: 'createdAt', title: '创建时间', width:'15%', unresize:true}
                ,{field: 'updatedAt', title: '更新时间', width:'15%', unresize:true}
                ,{title: '操作', align: 'left', width:0, toolbar: '#barDemo', unresize:true}
            ]]
        });
        table.on('checkbox(demo)', function(obj){
            console.log(obj)
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'edit'){
                updateRole(data.id);
            } else if(obj.event === 'del'){
                // layer.confirm('您确定要删除'+data.name+'角色吗？', {
                layer.confirm('该操作无法撤销，请确定是否删除?', {
                    skin: 'demo-class',
                    title: '警告',
                    btn: ['确认','返回'] //按钮
                }, function(index){
                    delRole(data.id,obj);
                    // obj.del();
                    layer.close(index);
                });
            }
        });



    //监听提交
    form.on('submit(roleSubmit)', function(data){
        addRole(data);

        return false;
    });


    //选中删除
    var  activeSelect = {
        getCheckData: function(){ //获取选中数据
            var checkStatus = table.checkStatus('roleLoad')
                ,data = checkStatus.data;
            delRoles(data);
            // layer.alert(JSON.stringify(data));
            // layer.msg('选中了：'+ data.length + ' 个');
            // layer.msg(checkStatus.isAll ? '全选': '未全选')
        }
    };
    $('.demoTable2 .layui-btn').on('click', function(){
        var type = $(this).data('type');
        activeSelect[type] ? activeSelect[type].call(this) : '';
    });


    var  active = {
            reload: function(){
                var Name = $('#Name');

                //执行重载
                table.reload('roleLoad', {
                    url: '/api/v1/system/roles',
                    method: 'Get',
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    ,where: {
                        name: Name.val(),
                    }
                }, 'data');
            }
        };
        $('.demoTable .layui-btn').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        function updateRole(id) {
            //isNaN是数字返回false
            if(id!=null && !isNaN(id)){
                window.location.href="/system/role/list/edit/"+id;
            }else{
                layer.alert("请求参数有误，请您稍后再试");
            }
        }
        function delRole(id,obj) {
            if(null!=id && !isNaN(id)){
                $.ajax({
                    url: "/api/v1/system/roles/del",
                    data: {'id': id},
                    type: "Delete",
                    success: function (data) {
                        layer.msg(data.msg);
                        obj.del();
                        // location.reload();
                        layer.closeAll();
                    },
                    error: function (data) {
                        alert(data.responseJSON.message);
                        layer.closeAll();
                    }
                });

            }
        }




}());
//显示添加弹窗

function openAddRole(){
    let $ = layui.jquery,
        layer = layui.layer
        ,form = layui.form;
    layer.open({
        type:1,
        title: "新增角色",
        fixed:false,
        resize :false,
        move: false,
        shadeClose: true,

        area: ['400px','250px'],
        // content: ['/admin/system/user/useropent','no'],
        content:$('#addRole'),
        end:function(){

        }
    });

}

//批量删除用户

function delRoles(elements) {

    layer.confirm('该操作无法撤销，请确定是否删除?', {
        skin: 'demo-class',
        title: '警告',
        btn: ['确认','返回'] //按钮

    }, function(index){
        layer.close(index);
        let i = 0;
        elements.forEach(function (element) {
            i++;
            //向服务端发送删除指令
            $.ajax({
                url: "/api/v1/system/roles/del",
                data:{'id':element.id},
                type:"DELETE",
                // dataType:"json",
                success:function(data){
                    if (i == elements.length) {
                        // layer.msg("操作成功");
                        location.reload();
                    }

                },
                error:function(data){
                    layer.alert(data.msg,function(){
                        layer.closeAll();

                    });
                }
            });
        })



    }, function(){
        layer.closeAll();
    });

}

//提交表单
function addRole(obj){
    //应该做一个更新自己的下线操作
    // var currentUser=$("#currentUser").html();
    if (obj.field.name.length < 2) {
        layer.alert("角色名称过短")
        return;
    }
    if (obj.field.roleCode.length < 2) {
        layer.alert("角色编码过短");
        return;
    }
    submitAjax(obj);
}

//添加用户
function submitAjax(obj){

    $.ajax({
        type: "POST",
        data: $("#roleForm").serialize(),
        url: "/api/v1/system/roles/add",
        handlers:{
            '_isView':'true'
        },
        success: function (data) {
            if (data ) {
                layer.alert("操作成功",function(){
                    layer.closeAll();

                    //$("#id").val("");
                    //加载页面
                    location.reload();
                });
            } else {
                layer.alert(data,function(){
                    layer.closeAll();
                });
            }
        },
        error: function (data) {
            var er = $.parseJSON(data.responseText);
            let error = er.errors;
            if (typeof error !== "undefined" && error !== null && error.length > 0) {
                layer.alert(error[0].message);
            }else {
                layer.alert(er.message);
            }


        }
    });
}

