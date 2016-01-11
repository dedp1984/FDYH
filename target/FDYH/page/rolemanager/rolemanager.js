Ext.application({
	name : '角色信息管理',
	launch : function() {

		var currentRoleId;
		var editFlag;
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : true,
			fields : [ 'roleid', 'rolename' ],
			proxy : {
				type : 'ajax',
				url : '../../action/role/queryRoleListByRoleName'
			}
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 100%',
			store : gridStore,
			flex:2,
			columns : [ {
				header : '角色ID',
				sortable : false,
				hideable : false,
				dataIndex : 'roleid'
			}, {
				header : '角色名称',
				flex:1,
				dataIndex : 'rolename'
			}, {
				header : '编辑角色',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().loadRecord(grid.getStore().getAt(rowIndex));
						queryGrid.anchor='100% 80%';
						queryGrid.updateLayout();
						editForm.show();
						editForm.getForm().findField('roleid').setReadOnly(true);
						editFlag=true;
					}
				} ]
			},{
				header : '删除角色',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						currentRoleId=grid.getStore().getAt(rowIndex).get('roleid');
						var selRoleName=grid.getStore().getAt(rowIndex).get('rolename');
						Ext.Msg.show({
							msg:'确定删除【'+selRoleName+'】?', 
							title:'删除角色', 
							buttons:Ext.Msg.OKCANCEL,
							fn:function(btn, text){
								if (btn == 'ok'){
							    	var request=Ext.Ajax.request({
										params: {
									        roleid: currentRoleId
									    },
										url:'../../action/role/deleteRole',
										success:function(response,options){
											Ext.Msg.alert('交易成功', '删除角色成功');
											gridStore.load();
										}
									});
							    }
							}
						})
					}
				} ]
			},{
				header : '设置角色权限',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/modify.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						currentRoleId=grid.getStore().getAt(rowIndex).get('roleid');
						currentRoleName=grid.getStore().getAt(rowIndex).get('rolename');
						authPanel.setTitle('设置角色权限-'+currentRoleName);
						authTreeStore.setProxy({
							type: 'ajax',
							url:'../../ajax/role/queryMenuTreeByRoleId'
						});
						authTreeStore.getProxy().extraParams = {
								'id' : currentRoleId
						};
						authTreeStore.load();
					}
				} ]
			} ],
			dockedItems : [ {
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		});
		var editForm =Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'20 20 0',
			anchor:'100% 30%',
			hidden:true,
			paramsAsHash: true,
			layout : 'form',
			items:[{
				layout:'hbox',
				bodyStyle:'background-color:#f5f5f5',
				items:[{
					xtype:'textfield',
					fieldLabel : '角色ID',
					name:'roleid'
			}]},{
				layout:'hbox',
				bodyStyle:'background-color:#f5f5f5',
				items:[,{
					xtype:'textfield',
					fieldLabel : '角色名称',
					name:'rolename',
					maxLength:20,
					allowBlank:false
					}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'保存',
				handler:function(){
					if(editForm.getForm().isValid()){
						editForm.getForm().submit({
							url:editFlag==false?'../../action/role/addRole':'../../action/role/modifyRole',
							method:'POST',
							success: function(form, action) {
		                        Ext.Msg.alert('交易成功', action.result.errors.msg);
		                        gridStore.load();
		                     },
		                     failure: function(form, action) {
		                         Ext.Msg.alert('交易失败', action.result.errors? action.result.errors.msg : 'No response');
		                     }
									
						})
					}
				}
			},{
				text:'返回',
				handler:function(){
					queryGrid.anchor='100% 100%';
					queryGrid.updateLayout();
					editForm.getForm().reset();
					editForm.hide();
				}
			}]
		});
		//角色信息列表显示
		var queryPanel = Ext.create('Ext.panel.Panel', {
			name:'queryPanel',
			title : '角色信息',
			flex : 3,
			layout : {
				type:'anchor'
			},
			
			dockedItems:[{
				xtype:'toolbar',
				dock:'top',
				items:[{
					glyph:'xf067@FontAwesome',
					text:'增加角色',
					handler:function(){
						
						queryGrid.anchor='100% 80%';
						queryGrid.updateLayout();
						editForm.show();
						editForm.getForm().findField('roleid').setReadOnly(false);
						editFlag=false;
					}
				}]
			}],
			items : [editForm,queryGrid]
		});
		
		
		var authTreeStore= Ext.create('Ext.data.TreeStore', {
		    root: {
		    	text:'绩效考核系统',
		        expanded: true
		    },
		    defaultRootId:0,
		    fields:[{name:'id',type:'string'},
		            {name:'text',type:'string'},
		            {name:'leaf',type:'boolean'},
		            {name:'checked',type:'boolean'}
		            ]
		});
		var authTree = Ext.create('Ext.tree.Panel', {
			store:authTreeStore,
			buttonAlign:'left',
			rootVisible:false,
			dockedItems:[{
				xtype:'toolbar',
				dock:'top',
				items:[{
					glyph:'xf0c7@FontAwesome',
					text:'保存',
					handler:function(){
						var menuIdList="";
						var index=0;
						for(var i=0;i<authTree.getChecked().length;i++){
							var rolemenu=new Object();
							rolemenu.roleid=currentRoleId;
							rolemenu.menuid=authTree.getChecked()[i].get('id');
							if(rolemenu.menuid==0)
								continue;
							if(index==0)
								menuIdList+=authTree.getChecked()[i].get('id');
							else
								menuIdList+=":"+authTree.getChecked()[i].get('id');
							index++;
						}
						var request=Ext.Ajax.request({
							params:{
								roleid:currentRoleId,
								menulist:menuIdList
							},
							url:'../../action/role/saveMenuTreeByRoleId',
							method:'POST',
							success:function(response,option){
								Ext.Msg.alert('交易成功', '修改角色权限成功');
							},
							failure:function(response,option){
								Ext.Msg.alert('交易失败', '修改角色权限失败');
							}
							
						});
					}
				},{
					text:'全选',
					glyph:'xf046@FontAwesome',
					handler:function(){
						var rootNode=authTree.getRootNode();
						updateCheckBoxTreeStatus(rootNode,true);
					}
				},{
					text:'反选' ,
					glyph:'xf096@FontAwesome',
					handler:function(){
						var rootNode=authTree.getRootNode();
						updateCheckBoxTreeStatus(rootNode,false);
					}
				}]
			}],
			listeners:{
				checkchange:updateCheckBoxTreeStatus
			}
		});
		//角色权限信息
		var authPanel = Ext.create('Ext.panel.Panel', {
			title : '设置角色权限',
			flex : 2,
			layout:'fit',
			items:[authTree]
		});
		Ext.create('Ext.container.Viewport', {
			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			items : [ queryPanel, authPanel ],
			renderTo : Ext.getBody()
		})
	}
})