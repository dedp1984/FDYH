
Ext.application({
	name : '个人质押存款信息管理',
	launch : function() {
		var editAction;
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
					items:[{
						xtype:'textfield',
						fieldLabel : '客户账号',
						width:300,
						name:'accountid',
						allowBlank:true
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
						allowBlank:true
						}]
				}]
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					hidden:true,
					items:[{
						xtype:'textfield',
						fieldLabel : '归属机构',
						name:'branchid',
						allowBlank:true
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					
					items:[{
						xtype:'textfield',
						fieldLabel : '归属机构',
						name:'branchname',
						allowBlank:true,
						readOnly:true,
						width:300
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
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						width:300,
						fieldLabel : '归属客户经理',
						name:'managerid',
						displayField:'accountname',  
				        valueField:'accountid',
				        forceSelection: true,
				        editable:false,
				        allowBlank:true,
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
				        		var branchId=queryForm.getForm().findField('branchid').getValue();
				        		queryForm.getForm().findField('managerid').getStore().getProxy().extraParams = {
									'branchid' : branchId
								};
				        		queryForm.getForm().findField('managerid').getStore().load();
				        	}
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
						var branchid=queryForm.getForm().findField('branchid').getValue();
						var managerid=queryForm.getForm().findField('managerid').getValue();
						gridStore.getProxy().extraParams = {
							'accountid' : accountId,
							'accountname':accountName,
							'branchid':branchid,
							'managerid':managerid
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
		
		
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			pageSize:20,
			fields : ['baseAccount','baseAccount.branch','baseAccount.branchid','baseAccount.branch.branchname','accountid','baseAccount.accountname',
			          'baseAccount.accounttype','binds','branch','startdate','enddate','tranamt','saleid'],
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
				url : '../../action/personPledge/queryPersonPledgeDetailList'
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
						return;
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
			anchor:'100% 50%',
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
						xtype:'textfield',
						fieldLabel : '客户账号',
						width:300,
						name:'accountid',
						allowBlank:false,
						listeners:{
							blur:function(cmp){
								Ext.Ajax.request({
								    url: '../../action/baseAccount/queryBaseAccountByAccountId',
								    params: {
								        'accountid':cmp.getValue()
								    },
								    success: function(response){
								    	
								        var data = Ext.JSON.decode(response.responseText);
								        if(data.success==false){
								        	
								        	alert("客户账号未建立分配关系");
								        	return;
								        }
								        bindPanel.removeAll();
								        var binds=data.items.binds;
								        editForm.getForm().findField('accountname').setValue(data.items.accountname);
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
										bindPanel.setDisabled(true);
								    },
								    failure:function(){
								    	alert("111");
								    }
								});
							}
						}
						},{
							width:300,
							xtype:'textfield',
							fieldLabel : '明细ID',
							name:'saleid',
							hidden:true
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
			},{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'datefield',
						fieldLabel : '起息日',
						name:'startdate',
						allowBlank:false,
						format:'Ymd',
						width:300
						}]
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'datefield',
						fieldLabel : '到期日',
						name:'enddate',
						allowBlank:false,
						format:'Ymd',
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
						fieldLabel : '购买金额',
						width:300,
						name:'tranamt',
						allowBlank:false
						},{
							xtype:'label',
							text:'(元)'
						}]
				}]
			},bindPanel],
			dockedItems : [{
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf0c7@FontAwesome', 
					text:'保存',
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
							}
							if(totalPercent>100){
								Ext.Msg.alert('信息提示','分配比例超过100,请重新分配');
								return;
							}
							if(totalPercent<100){
								Ext.Msg.alert('信息提示','分配比例小于100,请重新分配');
								return;
							}
							editForm.getForm().submit({
								url:editAction==true?'../../action/personPledge/updatePersonPledgeDetail':'../../action/personPledge/addPersonPledgeDetail',
								params:{
									bindlist:bindlist,
									accountid:accountid
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
			columns : [ Ext.create('Ext.grid.RowNumberer', {
				width : 30
			}), {
				header : '开户机构',
				dataIndex : 'baseAccount.branch.branchname',
				width:150
			}, {
				header : '客户账号',
				dataIndex : 'accountid',
				width:200
			}, {
				header : '客户名称',
				flex:1,
				dataIndex : 'baseAccount.accountname'
			},{
				header : '销售金额',
				width:120,
				dataIndex : 'tranamt'
			},{
				header : '开始日期',
				width:100,
				dataIndex : 'startdate',
				renderer:function(value){
					return value.substring(0,10);
				}
			},{
				header:'结束日期',
				dataIndex:'enddate',
				width:100,
				renderer:function(value){
					return value.substring(0,10);
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
						var record=grid.getStore().getAt(rowIndex);
						
//						var deepCopy= function(source) { 
//							var result={};
//							for (var key in source) {
//								result[key]=source[key];
//							   } 
//							   return result; 
//						}
//						var extend=function(o,n,override){
//							   for(var p in n)if(n.hasOwnProperty(p) && (!o.hasOwnProperty(p) || override))o[p]=n[p];
//						};
//						
//						var detailList=record.get('pledgeDetail');
//						detailStore.removeAll();
//						for(var i=0;i<detailList.length;i++){
//							var tmpRecord=deepCopy(record.data);
//							tmpRecord.pledgeDetail=[];
//							tmpRecord.pledgeDetail=detailList[i];
//							extend(tmpRecord,detailList[i]);
//							detailStore.add(tmpRecord);
//						}
						
						editForm.getForm().findField('accountid').setDisabled(true);
						var record=grid.getStore().getAt(rowIndex);
						editForm.getForm().findField('accountid').setValue(record.get('accountid'));
						editForm.getForm().findField('accountname').setValue(record.get('baseAccount').accountname);
						editForm.getForm().findField('tranamt').setValue(record.get('tranamt'));
						editForm.getForm().findField('saleid').setValue(record.get('saleid'));
						//var startdate = new Date(record.get('pledgeDetail').startdate);
						editForm.getForm().findField('startdate').setValue(record.get('startdate').substring(0,10));
						//var enddate = new Date(record.get('pledgeDetail').enddate);
						editForm.getForm().findField('enddate').setValue(record.get('enddate').substring(0,10));
						bindPanel.removeAll();
						var binds=record.get('binds');
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
						bindPanel.setDisabled(true);
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
						detailGrid.show();
					}
				} ]
			},{
				header : '删除',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del3.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
					var currentAccountId=grid.getStore().getAt(rowIndex).get('accountid');
					var currentAccountName=grid.getStore().getAt(rowIndex).get('accountname');
					var saleId=grid.getStore().getAt(rowIndex).get('saleid');
					Ext.Msg.show({
						msg:'确定删除【'+currentAccountName+'】?', 
						title:'信息提示', 
						buttons:Ext.Msg.OKCANCEL,
						fn:function(btn, text){
							if (btn == 'ok'){
						    	var request=Ext.Ajax.request({
									params: {
								        accountid: currentAccountId,
								        saleid:saleId
								    },
									url:'../../action/personPledge/deletePersonPledgeDetail',
									success:function(response,options){
										Ext.Msg.alert('信息提示', '删除成功');
										editPanel.hide();
										queryPanel.anchor='100% 100%';
										queryPanel.updateLayout();
										queryPanel.show();
										gridStore.load();
									}
								});
						    }
						}
					})
					}
				} ]
			}],
			dockedItems : [ {
				xtype:'toolbar',
				dock:'top',
				items:['->',{
					glyph:'xf067@FontAwesome',
					text:'录入质押存款信息',
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
						bindPanel.setDisabled(false);
						bindPanel.doLayout();
						detailGrid.hide();
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
			title:'个人质押存款信息管理',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		var detailStore = Ext.create('Ext.data.Store', {
			fields : ['branchid','branch.branchname','accountid','accountname','accounttype','submitid','submitdate','binds','branch','pledgeDetail',
			          'enddate','startdate','tranamt','saleid']
	    });
		var detailGrid=Ext.create('Ext.grid.Panel',{
			anchor:'100% 50%',
			store:detailStore,
			columns:[{
				header : '开户机构',
				dataIndex : 'branch.branchname',
				width:150
			},{
				header : '账号',
				dataIndex : 'accountid',
				width:150
			},{
				header : '户名',
				dataIndex : 'accountname',
				flex:1
			},{
				header:'交易金额',
				dataIndex:'tranamt',
				width:100
			},{
				header:'起息日',
				dataIndex:'startdate',
				width:100,
				 renderer:function(value){
		            	if(value=='')
		            		return '';
		            	
		            	return value.substring(0,10);
		            }
			},{
				header:'到期日',
				dataIndex:'enddate',
				width:100,
				 renderer:function(value){
		            	if(value=='')
		            		return '';
		            	return value.substring(0,10);
		            }
			},{
				header : '编辑质押存款信息',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						editForm.getForm().findField('accountid').setDisabled(true);
						var record=grid.getStore().getAt(rowIndex);
						editForm.getForm().findField('accountid').setValue(record.get('pledgeDetail').accountid);
						editForm.getForm().findField('accountname').setValue(record.get('accountname'));
						editForm.getForm().findField('tranamt').setValue(record.get('pledgeDetail').tranamt);
						editForm.getForm().findField('saleid').setValue(record.get('pledgeDetail').saleid);
						//var startdate = new Date(record.get('pledgeDetail').startdate);
						editForm.getForm().findField('startdate').setValue(record.get('pledgeDetail').startdate.substring(0,10));
						//var enddate = new Date(record.get('pledgeDetail').enddate);
						editForm.getForm().findField('enddate').setValue(record.get('pledgeDetail').enddate.substring(0,10));
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
						bindPanel.setDisabled(true);
						editAction=true;
						//queryPanel.hide();
						//editPanel.anchor='100% 100%';
						//editPanel.updateLayout();
						//editPanel.show();
					}
				} ]
			},{
				header : '删除',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/del3.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
					var currentAccountId=grid.getStore().getAt(rowIndex).get('accountid');
					var currentAccountName=grid.getStore().getAt(rowIndex).get('accountname');
					var saleId=grid.getStore().getAt(rowIndex).get('pledgeDetail').saleid;
					Ext.Msg.show({
						msg:'确定删除【'+currentAccountName+'】?', 
						title:'信息提示', 
						buttons:Ext.Msg.OKCANCEL,
						fn:function(btn, text){
							if (btn == 'ok'){
						    	var request=Ext.Ajax.request({
									params: {
								        accountid: currentAccountId,
								        saleid:saleId
								    },
									url:'../../action/personPledge/deletePersonPledgeDetail',
									success:function(response,options){
										Ext.Msg.alert('信息提示', '删除成功');
										editPanel.hide();
										queryPanel.anchor='100% 100%';
										queryPanel.updateLayout();
										queryPanel.show();
										gridStore.load();
									}
								});
						    }
						}
					})
					}
				} ]
			}]
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'个人质押存款信息录入',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'anchor',
			items:[editForm]
		});
		
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel,editPanel],
			renderTo : Ext.getBody()
			
		})
	}
})