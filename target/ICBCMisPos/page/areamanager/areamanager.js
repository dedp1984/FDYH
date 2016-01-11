Ext.application({
	name : '小区信息管理',
	launch : function() {
		
		var editAction;
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'areaid', 'areaname','address' ,'areainfo','phone','branchid','branch.branchname'],
			proxy : {
				type : 'ajax',
				actionMethods:{
					create: 'POST', 
					read: 'POST', 
					update: 'POST', 
					destroy: 'POST'
				},
				reader: {
		            type: 'json',
		            totalProperty: 'totalsize'
		        },
				url : '../../action/areamanager/queryAllSubAreaListByBranchId'
			}
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 80%',
			store : gridStore,
			flex:2,
			columns : [ Ext.create('Ext.grid.RowNumberer', {
				width : 30
			}), {
				header : '小区名称',
				dataIndex : 'areaname',
				flex:1
			}, {
				header : '编辑',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().reset();
						editForm.getForm().loadRecord(grid.getStore().getAt(rowIndex));
						editAction=true;
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
					}
				} ]
			},{
				header : '删除',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						var id=grid.getStore().getAt(rowIndex).get('areaid');
						var name=grid.getStore().getAt(rowIndex).get('areaname');
						Ext.Msg.show({
							msg:'确定删除【'+name+'】?', 
							title:'信息提示', 
							buttons:Ext.Msg.OKCANCEL,
							fn:function(btn, text){
								if (btn == 'ok'){
							    	var request=Ext.Ajax.request({
										params: {
									        'areaid': id
									    },
										url:'../../action/areamanager/deleteArea',
										success:function(response,options){
											Ext.Msg.alert('信息提示', '删除小区信息成功');
											gridStore.load();
										}
									});
							    }
							}
						})
					}
				} ]
			}],
			
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf067@FontAwesome',
					text:'增加小区',
					handler:function(){
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						editForm.getForm().reset();
						editAction=false;
					}
				}]
			}, {
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		});
		
		
		var editForm =Ext.create('Ext.form.Panel',{
			bodyBorder:false,
			paramsAsHash: true,
			layout : 'form',
			bodyPadding:'0 20 0',
			items:[{
				xtype:'container',
				border:false,
				layout:'column',
				items:[{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '所属区域',
						width:400,
						name:'branch.branchname',
						allowBlank:false,
						readOnly:true
						},{
							xtype:'button',
							glyph:'xf002@FontAwesome',
							handler:function(button,e){
								var treeStore= Ext.create('Ext.data.TreeStore', {
								    root: {
								    	text:'根节点',
								        expanded: true
								    },
								    nodeParam: 'id',
								    defaultRootId:0,
								    fields:[{name:'id',type:'string'},
								            {name:'text',type:'string'},
								            {name:'leaf',type:'boolean'},
								            {name:'parentid',type:'string'}
								            ],
								     proxy: {
												type:'ajax',
												method:'post',
												url:'../../action/branch/queryBranchTreeByAccountId'
											}
								});
								var treePanel = Ext.create('Ext.tree.Panel', {
									bodyPadding: '20 20 0',
									store:treeStore,
									rootVisible: false,
								    aoutLoad:true,
									listeners:{
										itemclick:function(view,record,item,index,e,opt){
											editForm.getForm().findField('branchid').setValue(record.get('id'));
											editForm.getForm().findField('branch.branchname').setValue(record.get('text'));
											win.close();
										}
									}
								});
								var win = Ext.create('Ext.window.Window', {
								    title: '选择区域',
								    collapsible: true,
								    animCollapse: true,
								    maximizable: true,
								    closeAction:'destory',
								    width: 300,
								    height: 400,
								    minWidth: 300,
								    minHeight: 200,
								    layout: 'fit',
								    items: [treePanel]
								});
								win.showAt(e.getXY());
							}
						
							}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '所属机构',
						name:'branchid',
						hidden:true
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '小区名称',
						name:'areaname',
						allowBlank:false
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '详细地址',
						name:'address'
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '物管电话',
						name:'phone'
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				hidden:true,
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '小区id',
						name:'areaid'
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						flex:1,
						xtype:'htmleditor',
						fieldLabel : '小区简介',
						name:'areainfo'
						}]
				}]
			}],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf0c7@FontAwesome',
					text:'保存',
					handler:function(){
						if(editForm.getForm().isValid()){
							editForm.getForm().submit({
								url:editAction==false?'../../action/areamanager/addArea':'../../action/areamanager/modifyArea',
								success:function(form,action){
									Ext.Msg.alert('信息提示', editAction==false?'增加成功':'修改成功');
									editPanel.hide();
									queryPanel.anchor='100% 100%';
									queryPanel.updateLayout();
									queryPanel.show();
									gridStore.load();
								},
								failure:function(form,action){
									Ext.Msg.alert('信息提示',editAction==false?"增加小区信息失败":"修改小区信息失败");
								}
							})
						}
						
					}
				},{
					glyph:'xf0e2@FontAwesome',
					text:'返回查询',
					handler:function(){
						editPanel.hide();
						queryPanel.anchor='100% 100%';
						queryPanel.updateLayout();
						queryPanel.show();
					}
				}]
			}]
		});
		
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'20 20 0',
			anchor:'100% 20%',
			paramsAsHash: true,
			boder:false,
			layout : 'form',
			items : [{
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.5,
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						flex:1,
						xtype:'textfield',
						fieldLabel : '小区名称',
						name:'areaname'
					}]
				}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				glyph:'xf002@FontAwesome',
				handler:function(){
					if(queryForm.getForm().isValid()){
						var areaname = queryForm.getForm().findField('areaname').getValue();
						gridStore.getProxy().extraParams = {
							'areaname' : areaname
						};
						gridStore.load();
					}
				}
			},{
				text:'重置',
				glyph:'xf021@FontAwesome',
				handler:function(){
					queryForm.getForm().reset();
				}
			}]
		});
		var queryPanel = Ext.create('Ext.panel.Panel', {
			name:'queryPanel',
			title : '小区信息',
			anchor:'100% 100%',
			layout : {
				type:'fit'
			},
			items : [queryGrid]
		});
		
		
	    
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'小区信息',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'fit',
			items:[editForm]
		});
		
		var treeStore= Ext.create('Ext.data.TreeStore', {
		    root: {
		    	text:'根节点',
		        expanded: true
		    },
		    nodeParam: 'id',
		    defaultRootId:'0',
		    fields:[{name:'id',type:'string'},
		            {name:'text',type:'string'},
		            {name:'leaf',type:'boolean'},
		            {name:'parentid',type:'string'}
		            ],
		     proxy: {
						type:'ajax',
						method:'post',
						url:'../../action/branch/queryBranchTreeByAccountId'
					}
		});
		
		var branchPanel = Ext.create('Ext.tree.Panel', {
			title: '区域信息',
			flex:1,
			width:400,
			bodyPadding: '20 20 0',
			store:treeStore,
			rootVisible: false,
		    aoutLoad:true,
			listeners:{
                itemclick:function(view, record, item, index, e, eOpts){
                	gridStore.getProxy().extraParams={
                		branchId:record.get('id')
                	},
                	gridStore.load();
                }
			}
		});
		
		var areaPanel=Ext.create('Ext.panel.Panel',{
			flex:3,
			layout :'anchor',
			border:false,
			items : [ queryPanel, editPanel ]
		})
		Ext.create('Ext.container.Viewport', {
			layout :{
				type:'hbox',
				align:'stretch'
			},
			items : [ branchPanel, areaPanel ],
			renderTo : Ext.getBody()
			
		})
	}
})