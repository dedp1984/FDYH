Ext.application({
	name : '账户管理',
	launch : function() {
		var transManagerid;
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
			columns : [{
				header : '用户ID',
				dataIndex : 'accountid',
				width:100
			}, {
				header : '姓名',
				dataIndex : 'accountname',
				width:250
			}, {
				header : '所属机构',
				flex:1,
				dataIndex : 'branch.branchname'
			},{
				header : '用户类型',
				dataIndex : 'property',
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
						return '分行部门领导';
					}
				}
			},{
				header : '离职操作',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().reset();						
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						transManagerid=grid.getStore().getAt(rowIndex).get('accountid');
						ownAccountStore.getProxy().extraParams = {
							'managerid' : transManagerid
						};
						ownAccountStore.load();
						transAccountStore.removeAll();
						
					}
				} ]
			}],
			
			dockedItems : [{
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		});
		

		
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'10 20 0',
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
			                ['5','虚拟客户经理']
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
		
		
		var ownAccountStore=Ext.create('Ext.data.Store', {
			autoLoad : false,
			pageSize:10000,
			fields : ['branchid','branch.branchname','accountid','accountname','accounttype','submitid','submitdate','binds','branch','percent'],
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
                    root:'items'
				},
				url : '../../action/baseAccount/queryBaseAccountList'
			}
		});
		var transAccountStore=Ext.create('Ext.data.Store', {
			fields : ['branchid','branchname','accountid','accountname','tomanagerid','tomanagername']
		});
		var ownAccountGrid=Ext.create('Ext.grid.Panel',{
			anchor:'100% 50%',
			title:'选择需变更关系账户',
			store:ownAccountStore,
			selType: 'checkboxmodel',
			columns : [ {
				header : '开户机构',
				dataIndex : 'branch.branchname',
				width:200
			},{
				header : '客户账号',
				dataIndex : 'accountid',
				width:200
			},{
				header : '客户名称',
				dataIndex : 'accountname',
				width:200
			},{
				header:'绩效占比',
				dataIndex:'percent',
				width:100,
				renderer:function(val){
					return (val*100)+'%';
				}
			}]
		});
		var transAcccountGrid=Ext.create('Ext.grid.Panel',{
			anchor:'100% 50%',
			store:transAccountStore,
			columns : [{
				header : '开户机构',
				dataIndex : 'branch.branchname',
				width:200
			},{
				header : '客户账号',
				dataIndex : 'accountid',
				width:200
			},{
				header : '客户名称',
				dataIndex : 'accountname',
				width:200
			},{
				header:'绩效占比',
				dataIndex:'percent',
				width:100,
				renderer:function(val){
					return (val*100)+'%';
				}
			},{
				header:'变更客户经理',
				dataIndex:'tomanagername'
			},{
				header : '撤销',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						record=grid.getStore().getAt(rowIndex)
						transAccountStore.remove(record);
						var data=new Array();
						data[0]=record.data;
						ownAccountStore.loadRawData(data,true);
					}
				} ]
			}]
		});
		
		var editForm =Ext.create('Ext.form.Panel',{
			anchor:'100% 100%',
			layout:'anchor',
			items:[ownAccountGrid,transAcccountGrid],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf0c7@FontAwesome',
					text:'保存',
					handler:function(){
						var transrecords=transAccountStore.getRange();
						if(transrecords.length==0){
							Ext.MessageBox.alert('信息提示','请选择变更记录');
							return;
						};
						var paramstr='';
						for(var i=0;i<transrecords.length;i++){
							if(i>0){
								paramstr+='|';
							}
							paramstr+=transrecords[i].data.accountid+'+';
							paramstr+=transrecords[i].data.managerid+'+';
							paramstr+=transrecords[i].data.tomanagerid;
							
						}
						if(editForm.getForm().isValid()){
							editForm.getForm().submit({		
								url:'../../action/BaseAccount/transManagerBind',
								params:{
									'paramstr':paramstr
								},
								success:function(form,action){
									Ext.Msg.alert('信息提示','变更客户经理信息成功');
									editPanel.hide();
									queryPanel.anchor='100% 100%';
									queryPanel.updateLayout();
									queryPanel.show();
									gridStore.load();
								},
								failure:function(form,action){
									Ext.Msg.alert('信息提示','变更客户经理信息失败');
								}
							})
						}
						
					}
				},{
					glyph:'xf0c7@FontAwesome',
					text:'设置变更信息',
					handler:function(){
						if(ownAccountGrid.getSelectionModel().hasSelection()){
							setTransWin.show();
							setTransForm.getForm().reset();
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
		
		var setTransForm=Ext.create('Ext.form.Panel',{
	    	layout:'form',
	    	items:[{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox',
						pack:'center'
					},
					border:false,
					hidden:true,
					items:[{
						xtype:'textfield',
						fieldLabel : '机构ID',
						name:'tobranchid'
						}]
				},{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '查询机构',
						name:'tobranchname',
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
											setTransForm.getForm().findField('tobranchid').setValue(record.get('id'));
											setTransForm.getForm().findField('tobranchname').setValue(record.get('text'));
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
							}]
		    },{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:1,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '客户经理',
						name:'managerid',
						displayField:'accountname',  
				        valueField:'accountid',
				        forceSelection: false,
				        editable:true,
				        allowBlank:true,
				        blankText:'请选择客户经理',
			            emptyText:'请选择客户经理',
			            store:{
				        	fields: ['accountid','accountname']
				        },
				        listeners:{
				        	expand:function(){
				        		var branchId=setTransForm.getForm().findField('tobranchid').getValue();
				        		if(branchId==''){
				        			return;
				        		}else{
				        			var propertys='1,2,3,5,6';
				        			setTransForm.getForm().findField('managerid').getStore().setProxy({
				        				type:'ajax',
										url:'../../action/account/queryAccountList',
										actionMethods:{
											create: 'POST', 
											read: 'POST', 
											update: 'POST', 
											destroy: 'POST'
										},
										reader: {  
							                    type:'json',
							                    root:'items'
							            }
				        			});
				        			setTransForm.getForm().findField('managerid').getStore().getProxy().extraParams = {
										'branchid' : branchId,
										'propertys':propertys
									};
				        			setTransForm.getForm().findField('managerid').getStore().load();
				        		}						        		
				        	}
				        }
					}]
				}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'选择',
				glyph:'xf00c@FontAwesome',
				handler:function(){
					if(setTransForm.getForm().isValid()){
						var managerid = setTransForm.getForm().findField('managerid').getValue();
						var managername=setTransForm.getForm().findField('managerid').rawValue;
						var records=ownAccountGrid.getSelectionModel().getSelection();
						var transdata=new Array();
						for(var i=0;i<records.length;i++){
						  var branchid=records[i].data.branchid;
						  var branchname=records[i].data.branch.branchname;
						  var accountid=records[i].data.accountid;
						  var accountname=records[i].data.accountname;
						  transdata[i]=records[i];
						  transdata[i].data.managerid=transManagerid;
						  transdata[i].data.tomanagerid=managerid;
						  transdata[i].data.tomanagername=managername;
						  ownAccountStore.remove(records[i]);
					    };
						transAccountStore.loadRawData(transdata,true);
						setTransWin.close();
					}
				}
			},{
				text:'关闭',
				glyph:'xf00d@FontAwesome',
				handler:function(){
					setTransWin.close();
				}
			}]
		});
		var setTransWin=Ext.create('Ext.window.Window',{
			title: '选择机构',
		    collapsible: true,
		    animCollapse: true,
		    maximizable: true,
		    closeAction:'destory',
		    width: 400,
		    height: 130,
		    layout: 'fit',
		    items: [setTransForm]			
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'客户经理离职设置',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'anchor',
			items:[editForm]
		});
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [ queryPanel, editPanel],
			renderTo : Ext.getBody()
			
		})
	}
})