Ext.application({
	name : '账户管理',
	launch : function() {
		
		var editAction;
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			pageSize:15,
			fields : [ 'accountid', 
			           'accountname',
			           'birthday' ,
			           'address',
			           'phone',
			           'email',
			           'property',
			           'status',
			           'expiredate',
			           'areaname',
			           'areaid',
			           'roles',
			           'branchid',
			           'branch.branchname',
			           'area.areaname',
			           'busiFeature'],
			proxy : {
				type : 'ajax',
				actionMethods:{
					create: 'POST', 
					read: 'POST', 
					update: 'POST', 
					destroy: 'POST'
				},
				reader: {  
                    type:'json',
                    root:'items',
                    totalProperty: 'totalsize'
				},
				url : '../../action/account/queryAccountList'
			}
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 80%',
			store : gridStore,
			flex:2,
			columns : [ {
				header : '所属机构',
				dataIndex : 'branch.branchname',
				width:150
			},  {
				header : '用户ID',
				dataIndex : 'accountid',
				width:100
			}, {
				header : '姓名',
				dataIndex : 'accountname',
				width:100
			},{
				header : '用户类型',
				dataIndex : 'property',
				width:100,
				renderer:function(value){
					if(value=='1'){
						return '行领导';
					}else if(value=='2'){
						return '部门领导';
					}else if(value=='3'){
						return '客户经理 ';
					}else if(value=='4'){
						return '系统管理员';
					}else if(value=='5'){
						return '虚拟客户经理';
					}else{
						return '高级用户';
					}
				}
			},{
				header:'具备角色',
				flex:1,
				dataIndex:'roles',
				renderer:function(value){
					var rolenames='';
					for(var i=0;i<value.length;i++){
						if(i==0){
							rolenames+=value[i].rolename;
						}else{
							rolenames+=","+value[i].rolename;
						}
					}
					return rolenames;
				}
			},{
				header : '编辑用户',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().reset();
						editForm.getForm().loadRecord(grid.getStore().getAt(rowIndex));
						var checkgroupItems=editForm.getForm().findField('busifeatures').items.items;
						var busiFeature=grid.getStore().getAt(rowIndex).get('busiFeature');
						for(var i=0;i<checkgroupItems.length;i++){
							var item=checkgroupItems[i];
							item.setValue(false);
							for(var j=0;j<busiFeature.length;j++){
								var feature=busiFeature[j];
								if(item.inputValue==feature.value){
									item.setValue(true);
									break;
								}
								
							}
						}
						/*
						checkgroupItems.each(function(f){
							busiFeature.each(function(b){
								if(b.value==f.inputValue){
									f.checked=true;
								}
							})
						})*/
						dsFrom.load({
							callback:function(){
								var roleList=grid.getStore().getAt(rowIndex).get('roles');
								var roleArray=new Array();
								for(i=0;i<roleList.length;i++){
									var roleid=roleList[i].roleid;
									roleArray.push(roleid);
								}
								editForm.getForm().findField('roleItemSelector').setValue(roleArray);
							}
						});
						editForm.getForm().findField('accountid').setReadOnly(true);
						editAction=true;
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
					}
				} ]
			},{
				header : '删除用户',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del.gif',
					//iconCls:'fa fa-times',
					handler : function(grid, rowIndex, colIndex, item, e) {
						var currentAccountId=grid.getStore().getAt(rowIndex).get('accountid');
						var currentAccountName=grid.getStore().getAt(rowIndex).get('accountname');
						Ext.Msg.show({
							msg:'确定删除【'+currentAccountName+'】?', 
							title:'信息提示', 
							buttons:Ext.Msg.OKCANCEL,
							fn:function(btn, text){
								if (btn == 'ok'){
							    	var request=Ext.Ajax.request({
										params: {
									        accountid: currentAccountId
									    },
										url:'../../action/account/deleteAccount',
										success:function(response,options){
											if(Ext.JSON.decode(response.responseText).success==true){
												Ext.Msg.alert('信息提示', '删除用户成功');
												gridStore.load();
											}else{
												alert('删除用户失败:'+Ext.JSON.decode(response.responseText).errors.errmsg);
											}
											
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
					text:'增加用户',
					handler:function(){
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						editForm.getForm().reset();
						dsFrom.load({
							callback:function(){
								editForm.getForm().findField('roleItemSelector').setValue([]);
							}
						});
						editForm.getForm().findField('accountid').setReadOnly(false);
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
		
		
		var dsFrom = Ext.create('Ext.data.ArrayStore', {
	        fields: ['roleid','rolename'],
	        proxy: {
	            type: 'ajax',
	            url: '../../ajax/role/queryRoleListByRoleName',
	            reader: 'json'
	        },
	        autoLoad: false,
	        sortInfo: {
	            field: 'roleid',
	            direction: 'ASC'
	        }
	    });
		
		var editForm =Ext.create('Ext.form.Panel',{
			bodyBorder:false,
			paramsAsHash: true,
			layout : 'form',
			items:[{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.4,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						width:400,
						fieldLabel : '用户ID',
						name:'accountid',
						allowBlank:false
						}]
				},{
					columnWidth:0.4,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						width:400,
						fieldLabel : '机构ID',
						name:'branchid',
						allowBlank:false,
						hidden:true
						
						}]
				},{
					columnWidth:0.4,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox',
						align:'middle'
					},
					border:false,
					items:[{
						xtype:'textfield',
						width:400,
						fieldLabel : '姓名',
						name:'accountname',
						allowBlank:false
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.4,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '所属机构',
						width:400,
						name:'branch.branchname',
						allowBlank:false,
						readOnly:true
						},{
							xtype:'button',
							//icon:'../../images/search-trigger.gif',
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
				},{
					columnWidth:0.4,
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						width:400,
						fieldLabel : '用户类型',
						name:'property',
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:false,
				        allowBlank:true,
				        blankText:'请选择用户类型',
			            emptyText:'请选择用户类型',
				        store:{  
				            type:'array',  
				            fields:['value','name'],  
				            data:[  
				                ['1','行领导'],  
				                ['2','部门领导'],
				                ['3','客户经理'],
				                ['4','系统管理员'],
				                ['5','虚拟客户经理'],
				                ['6','高级用户']
				            ] 
				        }
					}]
					
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.4,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
				        xtype: 'checkboxgroup',
				        name:'busifeatures',
				        fieldLabel: '业务范围',
				        allowBlank:false,
				        // Arrange checkboxes into two columns, distributed vertically
				        columns: 2,
				        vertical: true,
				        items: [
				            { boxLabel: '对公业务', width:100,name: 'busifeature', inputValue: '1'},
				            { boxLabel: '个人业务', width:100,name: 'busifeature', inputValue: '2'}
				        ]
				    }]
				}]
			},{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					xtype : 'itemselector',
					columnWidth:0.8,
					name:'rolelist',
		            id: 'roleItemSelector',
		            fieldLabel: '选择角色',
		            hideLabel : false,
		            imagePath: '../ux/images/',
		            store:dsFrom,
		            displayField: 'rolename',
		            valueField: 'roleid',
		            allowBlank: false,
		            msgTarget: 'side',
		            fromTitle: '可选角色',
		            toTitle: '已选角色'
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
								url:editAction==false?'../../action/account/addAccount':'../../action/account/modifyAccount',
								success:function(form,action){
									Ext.Msg.alert('信息提示', editAction==false?'增加用户成功':'修改用户成功');
									editPanel.hide();
									queryPanel.anchor='100% 100%';
									queryPanel.updateLayout();
									queryPanel.show();
									gridStore.load();
								},
								failure:function(form,action){
									Ext.Msg.alert('信息提示','增加用户失败');
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
			layout : 'form',
			items : [{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					hidden:true,
					items:[{
						xtype:'textfield',
						fieldLabel : '机构ID',
						name:'branchid'
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '查询机构',
						name:'branchname',
						width:300,
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
											queryForm.getForm().findField('branchid').setValue(record.get('id'));
											queryForm.getForm().findField('branchname').setValue(record.get('text'));
											win.close();
										}
									}
								});
								var win = Ext.create('Ext.window.Window', {
								    title: '选择机构',
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
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '用户ID',
						name:'accountid',
						width:300
						}]
				}]
			},{
			xtype:'panel',
			border:false,
			layout:'column',
			items:[{
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '用户姓名',
					name:'accountname',
					width:300
					}]
			},{
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'combobox',
					fieldLabel : '用户类型',
					name:'property',
					width:300,
					value:'',
					displayField:'name',  
			        valueField:'value',
			        forceSelection: false,
			        editable:false,
			        allowBlank:false,
		            store:{  
			            type:'array',  
			            fields:["value","name"],  
			            data:[  
			                ['','全部'],  
			                ['1','行领导'],
			                ['2','部门领导'],
			                ['3','客户经理'],
			                ['4','系统管理员'],
			                ['5','虚拟客户经理'],
			                ['6','高级用户']
			            ] 
			        }
				}]
			}]
		}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				glyph:'xf002@FontAwesome',
				handler:function(){
					if(queryForm.getForm().isValid()){
						var accountId = queryForm.getForm().findField('accountid').getValue();
						var accountName = queryForm.getForm().findField('accountname').getValue();
						var branchId=queryForm.getForm().findField('branchid').getValue();
						var property=queryForm.getForm().findField('property').getValue();
						gridStore.getProxy().extraParams = {
							'accountid' : accountId,
							'accountname':accountName,
							'branchid':branchId,
							'propertys':property
						};
						gridStore.currentPage=1;
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
		//角色信息列表显示
		var queryPanel = Ext.create('Ext.panel.Panel', {
			name:'queryPanel',
			title : '用户信息查询',
			anchor:'100% 100%',
			layout : {
				type:'anchor'
			},
			items : [queryForm,queryGrid]
		});
		
		
	    
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'用户信息管理',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'fit',
			items:[editForm]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [ queryPanel, editPanel ],
			renderTo : Ext.getBody()
			
		})
	}
})