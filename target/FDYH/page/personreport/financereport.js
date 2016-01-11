
Ext.application({
	name : '个人理财明细',
	launch : function() {
		var editAction;
		var detailExportUrl;
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'10 20 0',
			anchor:'100% 35%',
			paramsAsHash: true,
			layout : 'form',
			items : [{
				xtype:'panel',
				border:false,
				layout:'column',
				items:[{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox'
					},
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '理财类型',
						name:'accounttype',
						width:300,
						displayField:'name',  
				        valueField:'value',
				        value:'6,7',
				        forceSelection: false,
				        editable:false,
				        allowBlank:false,
				        blankText:'请选择理财类型',
			            emptyText:'请选择理财类型',
			            store:{  
				            type:'array',  
				            fields:["value","name"],
				            data:[
					                ['6,7','全部'],  
					                ['6','对公理财'],  
					                ['7','个人理财']
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
						name:'branchid'
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
											queryForm.getForm().findField('managerid').getStore().removeAll();
											queryForm.getForm().findField('managerid').setValue('');
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
								win.show(e.getX()-80,e.getY());
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
					xtype:'combobox',
					fieldLabel : '客户经理',
					name:'managerid',
					width:300,
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
			        		var branchId=queryForm.getForm().findField('branchid').getValue();
			        		if(branchId==''){
			        			return;
			        		}else{
			        			var propertys='1,2,3,5,6';
			        			queryForm.getForm().findField('managerid').getStore().setProxy({
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
				        		queryForm.getForm().findField('managerid').getStore().getProxy().extraParams = {
									'branchid' : branchId,
									'propertys':propertys
								};
				        		queryForm.getForm().findField('managerid').getStore().load();
			        		}						        		
			        	}
			        }
				}]
			},{
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'combobox',
					fieldLabel : '产品系列',
					name:'productid',
					width:300,
					displayField:'comment',  
			        valueField:'id',
			        value:'',
			        forceSelection: false,
			        editable:true,
			        allowBlank:true,
			        blankText:'请选择产品系列',
		            emptyText:'请选择产品系列',
		            store:{
			        	fields: ['id','comment'],
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
							url:'../../action/financeProduct/queryFinanceProductTypeList'
						}
			        },
			        listeners:{
			        	expand:function(){
			        		queryForm.getForm().findField('productid').getStore().load();
			        	}
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
					xtype:'datefield',
					fieldLabel : '起息日',
					name:'startdate',
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
					fieldLabel : '产品批次',
					name:'productbatch',
					width:300
				}]
			},{
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:{
					type:'hbox'
				},
				border:false,
				items:[{
					xtype:'combobox',
					fieldLabel : '购买渠道',
					name:'channel',
					width:300,
					value:'',
					displayField:'name',  
			        valueField:'value',
			        forceSelection: true,
			        editable:false,
			        allowBlank:false,
			        blankText:'请选择购买渠道',
		            emptyText:'请选择购买渠道',
		            store:{  
			            type:'array',  
			            fields:["value","name"],  
			            data:[
			                ['','全部'],  
			                ['1','手机'],  
			                ['2','网银'],
			                ['3','柜面']
			            ] 
			        }
				}]
			}]
		},{
			xtype:'panel',
			border:false,
			layout:'column',
			items:[{
				columnWidth:0.5,
				layout:'hbox',
				border:false,
				items:[{
					xtype:'combobox',
					width:300,
					fieldLabel : '是否质押',
					name:'iszy',
					value:'',
					displayField:'name',  
			        valueField:'value',
			        forceSelection: true,
			        editable:false,
			        allowBlank:false,
			        blankText:'请选择是否质押',
		            emptyText:'请选择是否质押',
			        store:{  
			            type:'array',  
			            fields:["value","name"],  
			            data:[ 
			                  ['','全部'],  
			                ['0','是'],  
			                ['1','否']
			            ] 
			        }
				}]
				
			},{
				columnWidth:0.5,  //该列占用的宽度，标识为50％
				layout:'hbox',
				border:false,
				items:[{
					xtype:'datefield',
					fieldLabel : '年日均截止日期',
					name:'calenddate',
					format:'Ymd',
					width:300
				}]
			}]
		}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				glyph:'xf002@FontAwesome',
				handler:function(){
					if(queryForm.getForm().isValid()){
						var startdate= Ext.util.Format.date(queryForm.getForm().findField('startdate').getValue(),'Ymd');
						var enddate = Ext.util.Format.date(queryForm.getForm().findField('enddate').getValue(),'Ymd');
						var branchid = queryForm.getForm().findField('branchid').getValue();
						var managerid = queryForm.getForm().findField('managerid').getValue();
						var accounttype = queryForm.getForm().findField('accounttype').getValue();
						var productbatch = queryForm.getForm().findField('productbatch').getValue();
						var productid = queryForm.getForm().findField('productid').getValue();
						var channel=queryForm.getForm().findField('channel').getValue();
						var iszy=queryForm.getForm().findField('iszy').getValue();
						var calenddate=Ext.util.Format.date(queryForm.getForm().findField('calenddate').getValue(),'Ymd');
						gridStore.setProxy({
							type: 'ajax',
				            url: '../../action/query/QueryFinanceCnt',
				            reader: {  
			                    type:'json'
							}
						});
						gridStore.getProxy().extraParams = {
							'branchid':branchid,
							'managerid':managerid,
							'accounttype':accounttype,
							'productid':productid,
							'productbatch':productbatch,
							'startdate':startdate,
							'enddate' : enddate,
							'channel':channel,
							'iszy':iszy,
							'calenddate':calenddate
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
		

		var gridStore = Ext.create('Ext.data.TreeStore', {
			autoLoad:false,
	        
	        fields: [
	 	            {name: 'name',     type: 'string'},
	 	            {name: 'cntamt',     type: 'string'},
	 	            {name: 'totalamt', type: 'string'},
	 	            {name: 'nodetype', type:'string'},
	 	            {name: 'totaltimebal', type:'string'},
	 	            {name: 'totalavg', type:'string'},
	 	           {name: 'avgenddate', type:'string'},
	 	            {name: 'id', type:'string'}
	 	        ]
	    });

		var queryGrid=Ext.create('Ext.tree.Panel', {
			anchor:'100% 65%',
		    title: '查询结果',
		    collapsible: false,
	        useArrows: true,
	        rootVisible: false,
	        store: gridStore,
	        multiSelect: true,
	        columnLines:true,
	        rowLines:true,
	        columns: [{
	            xtype: 'treecolumn', //this is so we know which column will show the tree
	            text: '机构名称',
	            width: 280,
	            sortable: true,
	            dataIndex: 'name',
	            locked: true
	        }, {
	            text: '销售总笔数',
	            width: 100,
	            dataIndex: 'cntamt',
	            sortable: true,
	            border:true,
	            align:'right'
	        }, {
	            text: '销售总金额',
	            width: 150,
	            dataIndex: 'totalamt',
	            sortable: true,
	            align:'right'
	        },{
	            text: '时点余额',
	            width: 150,
	            dataIndex: 'totaltimebal',
	            sortable: true,
	            align:'right'
	        },{
	            text: '年日均余额',
	            width: 150,
	            dataIndex: 'totalavg',
	            sortable: true,
	            align:'right'
	        },{
	            text: '余额统计时段',
	            width: 150,
	            dataIndex: 'avgenddate',
	            sortable: true
	        },{
				header : '查看销售明细',
				xtype : 'actioncolumn',
				width:100,
				align:'center',
				items : [ {
					icon:'../../images/detail.gif',
					handler : function(grid, rowIndex, colIndex, item, e) {
						var record=grid.getStore().getAt(rowIndex);
						var nodetype=record.get('nodetype');
						var startdate= Ext.util.Format.date(queryForm.getForm().findField('startdate').getValue(),'Ymd');
						var enddate = Ext.util.Format.date(queryForm.getForm().findField('enddate').getValue(),'Ymd');
						var branchid='';
						var managerid='';
						if(nodetype=='1')
							branchid= record.get('id');
						else if(nodetype=='2')
							managerid = record.get('id');
						else
							branchid=queryForm.getForm().findField('branchid').getValue();
						var accounttype = queryForm.getForm().findField('accounttype').getValue();
						var productbatch = queryForm.getForm().findField('productbatch').getValue();
						var productid = queryForm.getForm().findField('productid').getValue();
						var channel=queryForm.getForm().findField('channel').getValue();
						var iszy=queryForm.getForm().findField('iszy').getValue();
						detailExportUrl=Ext.String.format(
					            '../../action/export/ExportFinanceDetail?branchid={0}&managerid={1}&accounttype={2}&productid={3}'+
								'&productbatch={4}&startdate={5}&enddate={6}&channel={7}&iszy={8}',
								branchid,managerid,accounttype,productid,productbatch,startdate,enddate,channel,iszy);
						detailGridStore.getProxy().extraParams = {
							'branchid':branchid,
							'managerid':managerid,
							'accounttype':accounttype,
							'productid':productid,
							'productbatch':productbatch,
							'startdate':startdate,
							'enddate' : enddate,
							'channel':channel
						};
						
						detailGridStore.load();
						queryPanel.hide();
						editPanel.anchor='100% 100%';
						editPanel.updateLayout();
						editPanel.show();
					}
				} ]
			}]
		    
		});
		var queryPanel=Ext.create('Ext.panel.Panel',{
			layout:'anchor',
			title:'个人理财明细查询',
			anchor:'100% 100%',
			items:[queryForm,queryGrid]
		});
		
		var detailGridStore=Ext.create('Ext.data.Store', {
			autoLoad:false,
			pageSize:15,
			proxy:{
				type: 'ajax',
				actionMethods:{
					create: 'POST', 
					read: 'POST', 
					update: 'POST', 
					destroy: 'POST'
				},
	            url: '../../action/query/queryFinanceDetail',
	            reader: {  
                    type:'json',
                    root:'items',
                    totalProperty: 'totalsize'
				}
			},
	        fields: [
	 	            {name: 'accounttype', type: 'string'},
	 	            {name: 'branchid', type: 'string'},
	 	            {name: 'branchname',     type: 'string'},
	 	            {name: 'accountid', type: 'string'},
	 	            {name: 'accountname', type: 'string'},
	 	            {name: 'tranamt', type: 'string'},
	 	            {name: 'productbatch', type: 'string'},
	 	            {name: 'productname', type: 'string'},
	 	            {name: 'startdate', type: 'string'},
	 	            {name: 'enddate', type: 'string'},
	 	            {name: 'channel',type:'string'},
	 	             {name: 'channelname',type:'string'},
	 	             {name: 'iszy',type:'string'}
	 	        ]
	    });
		
		var detailGrid=Ext.create('Ext.grid.Panel', {
			anchor:'100% 100%',
	        store: detailGridStore,
	        columns: [{
	            text: '账户类型',
	            width: 70,
	            sortable: true,
	            dataIndex: 'accounttype',
	            renderer:function(value){
	            	if(value=='6')
	            		return '对公理财';
	            	else
	            		return '个人理财';
	            }
	        }, {
	            text: '所属机构',
	            flex:1,
	            dataIndex: 'branchname',
	            sortable: true,
	            border:true
	        },{
	            text: '客户账号',
	            width: 150,
	            dataIndex: 'accountid',
	            sortable: true,
	            border:true
	        },{
	            text: '客户名称',
	            width: 150,
	            dataIndex: 'accountname',
	            sortable: true
	        },{
	            text: '交易金额',
	            width: 100,
	            dataIndex: 'tranamt',
	            sortable: true,
	            align:'right'
	        }, {
	            text: '起息日',
	            width: 80,
	            dataIndex: 'startdate',
	            renderer:function(value){
	            	if(value=='')
	            		return '';
	            	else
	            		return value.substring(0,10);
	            },
	            sortable: true
	        },{
	            text: '到期日',
	            width: 80,
	            dataIndex: 'enddate',
	            renderer:function(value){
	            	if(value=='')
	            		return '';
	            	else
	            		return value.substring(0,10);
	            },
	            sortable: true
	        },{
	            text: '购买渠道',
	            width: 70,
	            dataIndex: 'channelname',
	            sortable: true
	        },{
	            text: '产品类型',
	            width: 120,
	            dataIndex: 'productname',
	            sortable: true
	        },{
	            text: '产品批次',
	            width:110,
	            dataIndex: 'productbatch',
	            sortable: true
	        },{
				header:'是否质押',
				dataIndex:'iszy',
				renderer:function(value){
					if(value=='1'){
						return '否';
					}else{
						return '是';
					}
				},
				width:70
			}],
	        dockedItems : [{
	       				xtype:'toolbar',
	    				dock:'top',
	    				items:['->',{
	    					glyph:'xf067@FontAwesome',
	    					text:'导出EXCEL',
	    					handler:function(){
	    						window.location.href=detailExportUrl;	    						
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
	    			},{
				xtype : 'pagingtoolbar',
				store : detailGridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]
		    
		});
		var editPanel =Ext.create('Ext.panel.Panel',{
			name:'editPanel',
			title:'理财产品明细',
			anchor:'100% 100%',
			bodyBorder:false,
			hidden:true,
			layout:'anchor',
			items:[detailGrid]
		});
		Ext.create('Ext.container.Viewport', {
			layout :'anchor',
			items : [queryPanel,editPanel],
			renderTo : Ext.getBody()
			
		})
	}
})