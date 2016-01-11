
Ext.application({
	name : '绑定客户管理',
	launch : function() {
		var editAction;
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'10 20 0',
			anchor:'100% 20%',
			paramsAsHash: true,
			layout:'form',
			items:[{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[ {
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '客户账号',
						name:'accountid',
						width:300
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '客户名称',
						name:'accountname',
						width:300
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[ {
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '账户类型',
						name:'accounttype',
						width:300,
						displayField:'name',  
				        valueField:'value',
				        value:'',
				        forceSelection: false,
				        editable:true,
				        blankText:'请选择账户类型',
			            emptyText:'请选择账户类型',
				        store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[  
				                  ['','全部'], 
				                  ['1','对公活期账户'],  
					                ['2','对公定期账户'],
					                ['3','对公贷款账户'],
					                ['4','个人活期账户'],
					                ['5','个人定期账户'],
					                ['6','对公理财账户'],
					                ['7','个人理财账户'],
					                ['8','个人质押存款账户']
				            ] 
				        }
					}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '审批状态',
						name:'status',
						value:'',
						width:300,
						displayField:'name',  
				        valueField:'value',
				        forceSelection: false,
				        editable:true,
				        blankText:'请选择审批状态',
			            emptyText:'请选择审批状态',
				        store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[  
				                  ['','全部'],
				                  ['0','审批中'],  
					                ['1','审批通过'],
					                ['9','审批拒绝']
					               
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
						var accountType = queryForm.getForm().findField('accounttype').getValue();
						var status = queryForm.getForm().findField('status').getValue();
						gridStore.getProxy().extraParams = {
							'accountid' : accountId,
							'accountname':accountName,
							'accounttype':accountType,
							'status':status
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
		
		
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : ['branchid','branch.branchname','accountid','accountname','accounttype','submitid','submitdate','checkid','checkdate','binds','branch','status'],
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
				url : '../../action/nobindaccount/queryCheckdBindAccountList'
			}
		});
		
		Ext.define('bindForm',{
			extend : 'Ext.form.Panel',
			border:false,
			layout:'column',
			name:'bindForm',
			items:[{
				columnWidth:0.3,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				hidden:true,
				items:[{
					xtype:'textfield',
					fieldLabel : '归属机构',
					name:'toBranchid',
					allowBlank:false
					}]
			},{
				columnWidth:0.25,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'textfield',
					fieldLabel : '归属机构',
					name:'toBranchname',
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
											url:'../../action/branch/queryBranchTree'
										}
							});
							var treePanel = Ext.create('Ext.tree.Panel', {
								bodyPadding: '20 20 0',
								store:treeStore,
								rootVisible: false,
							    aoutLoad:true,
								listeners:{
									itemclick:function(view,record,item,index,e,opt){
										var parent=button.findParentByType('form');
										parent.getForm().findField('toBranchid').setValue(record.get('id'));
										parent.getForm().findField('toBranchname').setValue(record.get('text'));
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
				columnWidth:0.25,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'combobox',
					fieldLabel : '归属客户经理',
					name:'toManager',
					displayField:'accountname',  
			        valueField:'accountid',
			        forceSelection: true,
			        editable:false,
			        allowBlank:false,
			        autoLoad:false,
			        blankText:'选择归属客户经理',
		            emptyText:'选择归属客户经理',
			        store:{
			        	fields: ['accountid','accountname'],
			        	proxy: {
							type:'ajax',
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
							url:'../../action/account/querySysAccountListByBranchId'
						}
			        },
			        listeners:{
			        	expand:function(){
			        		var parent=this.findParentByType('form');
			        		var branchId=parent.getForm().findField('toBranchid').getValue();
			        		parent.getForm().findField('toManager').getStore().getProxy().extraParams = {
								'branchid' : branchId
							};
			        		parent.getForm().findField('toManager').getStore().load();
			        	}
			        }
				}]
			},{
				columnWidth:0.2,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'numberfield',
					fieldLabel:'绩效占比',
					width:150,
					lableWidth:80,
					name:'percent',
					allowBlank:false,
					value:100,
					minValue: 1,
					maxValue:100
					}]
			},{
				columnWidth:0.2,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				
				items:[{
					xtype:'combobox',
					fieldLabel : '账户来源',
					name:'fromType',
					width:200,
					labelWidth:80,
					displayField:'name',  
			        valueField:'value',
			        forceSelection: true,
			        editable:false,
			        allowBlank:false,
			        blankText:'请选择账户来源',
		            emptyText:'请选择账户来源',
			        store:{  
			            type:'array',  
			            fields:["value","name"],  
			            data:[  
			                ['1','自营'],  
			                ['2','交办']
			            ] 
			        }
				}]
			},{
				columnWidth:0.1,  //该列占用的宽度，标识为50％
				layout:{
					type:'hbox',
					pack:'center'
				},
				border:false,
				items:[{
					xtype:'button',
					text:'增加',
					handler:function(){
						var length=bindPanel.items.length;
						var bindForm=new Ext.create('bindForm');
						bindForm.id='bindForm'+length;
						bindPanel.insert(length,bindForm);
						bindPanel.doLayout();
					}
					},{
						xtype:'button',
						text:'删除',
						handler:function(button){
							var parent=button.findParentByType('form');
							bindPanel.remove(parent);
							if(bindPanel.items.length==0){
								var bindForm=new Ext.create('bindForm');
								bindPanel.insert(length,bindForm);
							}
							bindPanel.doLayout();
						}
						}]
			}]
		});

		
		var bindPanel=Ext.create('Ext.form.FieldSet',{
			title:'归属信息',
			border:true
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
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '账户类型',
						name:'accounttype',
						width:300,
						displayField:'name',  
				        valueField:'value',
				        forceSelection: true,
				        editable:false,
				        allowBlank:false,
				        blankText:'请选择账户类型',
			            emptyText:'请选择账户类型',
				        store:{  
				            type:'array',  
				            fields:["value","name"],  
				            data:[  
				                 ['1','对公活期账户'],  
				                ['2','对公定期账户'],
				                ['3','对公贷款账户'],
				                ['4','个人活期账户'],
				                ['5','个人定期账户'],
				                ['6','对公理财账户'],
				                ['7','个人理财账户'],
				                ['8','个人质押存款账户']
				            ] 
				        }
					}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					hidden:true,
					items:[{
						xtype:'textfield',
						fieldLabel : '机构ID',
						name:'branchid',
						allowBlank:false
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '管理机构',
						name:'branchname',
						width:300,
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
												url:'../../action/branch/queryBranchTree'
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
											editForm.getForm().findField('branchname').setValue(record.get('text'));
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
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '客户账号',
						width:300,
						name:'accountid',
						allowBlank:false
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '账户名称',
						width:300,
						name:'accountname',
						allowBlank:false
						}]
				}]
			},,{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'datefield',
						fieldLabel : '提交日期',
						name:'submitdate',
						allowBlank:false,
						readOnly:true,
						format:'Ymd',
						width:300
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'datefield',
						fieldLabel : '审批日期',
						name:'checkdate',
						allowBlank:false,
						readOnly:true,
						format:'Ymd'
						}]
				}]
			},bindPanel],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf0c7@FontAwesome', 
					text:'保存',
					id:'save',
					handler:function(){
						if(editForm.getForm().isValid()){
							var bindLen=bindPanel.items.length;
							var bindlist='';
							var accountid=editForm.getForm().findField('accountid').getValue();
							var totalPercent=0;
							for(var i=0;i<bindLen;i++){
								var bindForm=bindPanel.items.getAt(i).getForm();
								var toBranchid=bindForm.findField('toBranchid').getValue();
								var toManager=bindForm.findField('toManager').getValue();
								var percent=bindForm.findField('percent').getValue();
								totalPercent+=parseInt(percent);
								var type=bindForm.findField('fromType').getValue();
								bindlist+=toBranchid+','+toManager+','+percent+','+type+'#';
							};
							if(totalPercent>100){
								Ext.Msg.alert('信息提示','分配比例超过100,请重新分配');
								return;
							};
							editForm.getForm().submit({
								url:'../../action/noBindAccount/reSubmitBindAccountToManager',
								params:{
									accountid:accountid,
									bindlist:bindlist
								},
								success:function(form,action){
									Ext.Msg.alert('信息提示', '操作成功');
									editPanel.hide();
									queryPanel.anchor='100% 100%';
									queryPanel.updateLayout();
									queryPanel.show();
									gridStore.load();
								},
								failure:function(form,action){
									Ext.Msg.alert('信息提示','操作失败');
								}
							})
						}
						
					}
				},{
					glyph:'xf0e2@FontAwesome',
					text:'返回',
					handler:function(){
						editPanel.hide();
						queryPanel.anchor='100% 100%';
						queryPanel.updateLayout();
						queryPanel.show();
					}
				}]
			}]
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 80%',
			store : gridStore,
			flex:2,
			columns : [ {
				header : '管理机构',
				dataIndex : 'branch.branchname',
				width:150
			}, {
				header : '客户账号',
				dataIndex : 'accountid',
				width:200
			}, {
				header : '客户名称',
				flex:1,
				dataIndex : 'accountname'
			},{
				header : '类型',
				dataIndex : 'accounttype',
				width:100,
				renderer:function(value){
					if(value=='1'){
						return '对公活期账户';
					}else if(value=='2'){
						return '对公定期账户';
					}else if(value=='3'){
						return '对公贷款账户 ';
					}else if(value=='4'){
						return '个人活期账户';
					}else if(value=='5'){
						return '个人定期账户';
					}else if(value=='6'){
						return '对公理财账户';
					}else if(value=='7'){
						return '个人理财账户';
					}else{
						return '个人质押存款账户'
					}
				}
			},{
				header : '审批结果',
				dataIndex : 'status',
				width:100,
				renderer:function(value){
					if(value=='0'){
						return '待审批';
					}else if(value=='1'){
						return '审批通过';
					}else if(value=='9'){
						return '审批拒绝 ';
					}
				}
			},{
				header : '查看详细',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().reset();
						editForm.getForm().findField('accountid').setDisabled(true);
						editForm.getForm().loadRecord(grid.getStore().getAt(rowIndex));
						var status=grid.getStore().getAt(rowIndex).get('status');
						if(status=='9'){
							Ext.getCmp('save').hidden=false;
							Ext.getCmp('save').setText('重新提交');
						}else{
							Ext.getCmp('save').hidden=true;
						}
						
						var branchname=grid.getStore().getAt(rowIndex).get('branch').branchname;
						var branchid=grid.getStore().getAt(rowIndex).get('branch').branchid;
						//var submitdate=new Date(grid.getStore().getAt(rowIndex).get('submitdate'));
						var submitdate=grid.getStore().getAt(rowIndex).get('submitdate').substring(0,10);
						//var checkdate=new Date(grid.getStore().getAt(rowIndex).get('checkdate'));
						var checkdate=grid.getStore().getAt(rowIndex).get('checkdate').substring(0,10);
						editForm.getForm().findField('branchid').setValue(branchid);
						editForm.getForm().findField('branchname').setValue(branchname);
						editForm.getForm().findField('submitdate').setValue(submitdate);
						editForm.getForm().findField('checkdate').setValue(checkdate);
						bindPanel.removeAll();
						var binds=grid.getStore().getAt(rowIndex).get('binds');
						for(var i=0;i<binds.length;i++){
							var bind=binds[i];
							var bindForm=new Ext.create('bindForm');
							var toBranchid=bind.branchid;
							var toBranchname=bind.branchname;
							var toManager=bind.managerid;
							var percent=parseFloat(bind.percent)*100;
							var type=bind.type;
							
			        		bindForm.getForm().findField('toManager').getStore().getProxy().extraParams = {
								'branchid' : toBranchid
							};
			        		bindForm.getForm().findField('toManager').getStore().load();
			        		
							bindForm.getForm().findField('toBranchid').setValue(toBranchid);
							bindForm.getForm().findField('toBranchname').setValue(toBranchname);
							bindForm.getForm().findField('fromType').setValue(type);
							bindForm.getForm().findField('toManager').setValue(toManager);
							bindForm.getForm().findField('percent').setValue(percent);
							bindForm.getForm().findField('fromType').setValue(type);
							bindForm.id='bindForm'+i;
							bindPanel.insert(i,bindForm);
						}
						editAction=true;
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
					}
				} ]
			}],
			
			dockedItems : [ {
				xtype:'toolbar',
				dock:'top',
				hidden:true,
				items:['->',{
					glyph:'xf067@FontAwesome',
					text:'增加客户关系',
					handler:function(){
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						editForm.getForm().reset();
						editForm.getForm().findField('accountid').setDisabled(false);
						bindPanel.removeAll();
						var length=bindPanel.items.length;
						var bindForm=new Ext.create('bindForm');
						bindForm.id='bindForm'+length;
						bindPanel.insert(length,bindForm);
						bindPanel.doLayout();
						editAction=false;
					}
				}]
			},{
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		});
		var queryPanel=Ext.create('Ext.panel.Panel',{
			layout:'anchor',
			title:'审批结果查询',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'详细信息',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'fit',
			items:[editForm]
		});
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel,editPanel],
			renderTo : Ext.getBody()
			
		})
	}
})