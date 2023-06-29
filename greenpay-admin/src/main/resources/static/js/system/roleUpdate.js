/**
 * 权限列表
 */
//选中的复选框


!(function () {
    console.log(userRoles);
    let $ = layui.jquery;
    var tree;
    layui.use(['form', 'tree', 'layer', 'jquery'], function () {
        var form = layui.form;
        var layer = layui.layer;
        tree = layui.tree;
        //监听提交
        getTreeData(tree);


        form.on('submit(updateRoleSumbit)', function (data) {

            var array = new Array();
            //获取选中的权限id
            var checkedData = tree.getChecked('demoId1'); //获取选中节点的数据

            for (let j = 0; j < checkedData.length; j++) {
                var node = checkedData[j];
                let childs = getChildNode(node);
                childs.push(node.id);
                array.push(childs.concat());
                nodeIds.length = 0;
            }

            //校验是否授权
            var permIds = array.join(",");
            // console.log("permIds"+permIds)
            if (permIds == null || permIds == '') {
                layer.alert("请给该角色添加权限菜单！")
                return false;
            }
            var id = data.field.id;
            var name = data.field.name;
            var roleCode = data.field.roleCode;
            var role = {id:id,name:name,roleCode:roleCode,permIds:permIds}

            $.ajax({
                type: "PUT",
                data: role,
                url: "/admin/api/v1/system/roles",
                success: function (data) {
                    if (data ) {
                        layer.alert("操作成功", function () {
                            layer.closeAll();
                            window.location.href="/admin/system/role/list";
                        });
                    } else {
                        layer.alert("操作请求错误，请您稍后再试");
                    }
                },
                error: function (data) {
                    alert(data.responseJSON.message);
                    layer.closeAll();
                }
            });
            return false;
        });

    });



    function getTreeData() {
        $.ajax({
            type: "get",
            url: "/admin/api/v1/system/menus/roleTree",
            data:{"size":100},
            success: function (data) {
                let menus = data;
                if (menus != null) {
                    initTree(menus.records);
                } else {
                    layer.alert(menus);
                }
            },
            error: function () {
                layer.alert("获取数据错误，请您稍后再试");
            }
        });
    }

    function initTree(data) {
        layui.use('util', function () {

            util = layui.util;

            //渲染
            let a =  listToTreeJson(data);
            var inst1 = tree.render({
                id: 'demoId1',
                elem: '#perm', //指定元素
                target: '_blank', //是否新选项卡打开（比如节点返回href才有效）
                showCheckbox: true //是否显示复选框
                ,isJump: true //是否允许点击节点时弹出新窗口跳转
                ,click: function(obj){
                var data = obj.data;  //获取当前点击的节点数据
                // layer.msg('状态：'+ obj.state + '<br>节点数据：' + JSON.stringify(data));
                },
                skin: 'shihuang',//皮肤
                data: listToTreeJson(data)
            });

            //按钮事件
            // util.event('lay-demo', {
            //     getChecked: function (othis) {
            //         var checkedData = tree.getChecked('demoId1'); //获取选中节点的数据
            //
            //         layer.alert(JSON.stringify(checkedData), {shade: 0});
            //         console.log(checkedData);
            //     }
            //     , setChecked: function () {
            //         tree.setChecked('demoId1', [12, 16]); //勾选指定节点
            //     }
            //     , reload: function () {
            //         //重载实例
            //         tree.reload('demoId1', {});
            //
            //     }
            // });
        });

    }

    /**
     * 获取所有子节点的id数组
     * @param obj
     * @returns {Array}
     */
    let nodeIds = [];
    function getChildNode(obj) {

        if (obj != null) {
            if (obj.children.length > 0) {
                $.each(obj.children, function (k, v) {
                    nodeIds.push(v.id);
                    getChildNode(v);
                });
            }

        }
        return nodeIds;
    }

    /**
     * list转化为tree结构的json数据
     */
    function listToTreeJson(data) {
        //data不能为null，且是数组
        if (data != null && (data instanceof Array)) {
            //递归转化
            var getJsonTree = function (data, parentId) {
                var itemArr = [];
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    if (parentId != null && node.parentId == parentId) {

                        var newNode = {
                            title: node.title,
                            checked: userRoles.includes(node.id),
                            spread: true,
                            id: node.id,
                            pid: node.parentId,
                            children: getJsonTree(data, node.id)
                        };
                        itemArr.push(newNode);
                    }
                }
                return itemArr;
            }
            // return JSON.stringify(getJsonTree(data,''));
            return getJsonTree(data, 0);
        }
        // console.log(JSON.stringify(getJsonTree(data,'')));

    }}());