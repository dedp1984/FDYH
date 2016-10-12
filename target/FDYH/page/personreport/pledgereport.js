
Ext.application({
	name : '个人理财明细',
	launch : function() {
		var editAction;
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'10 20 0',
			anchor:'100% 25%',
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
				},{
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					layout:{
						type:'hbox'
					},
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
						var calenddate=Ext.util.Format.date(queryForm.getForm().findField('calenddate').getValue(),'Ymd');
						gridStore.setProxy({
							type: 'ajax',
				            url: '../../action/query/QueryPledgeCnt',
				            reader: {  
			                    type:'json'
							}
						});
						gridStore.getProxy().extraParams = {
							'branchid':branchid,
							'managerid':managerid,
							'startdate':startdate,
							'enddate' : enddate,
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
			anchor:'100% 75%',
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
	            width: 300,
	            sortable: true,
	            dataIndex: 'name',
	            locked: true
	        }, {
	            text: '总笔数',
	            width: 100,
	            dataIndex: 'cntamt',
	            sortable: true,
	            border:true
	        }, {
	            text: '总金额',
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
						detailGridStore.getProxy().extraParams = {
							'branchid':branchid,
							'managerid':managerid,
							'startdate':startdate,
							'enddate' : enddate
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
			pageSize:20,
			proxy:{
				type: 'ajax',
				actionMethods:{
					create: 'POST', 
					read: 'POST', 
					update: 'POST', 
					destroy: 'POST'
				},
	            url: '../../action/query/queryPledgeDetail',
	            reader: {  
                    type:'json',
                    root:'items',
                    totalProperty: 'totalsize'
				}
			},
	        fields: [
	 	            {name: 'branchid', type: 'string'},
	 	            {name: 'branchname',     type: 'string'},
	 	            {name: 'accountid', type: 'string'},
	 	            {name: 'accountname', type: 'string'},
	 	            {name: 'tranamt', type: 'string'},
	 	            {name: 'startdate', type: 'string'},
	 	            {name: 'enddate', type: 'string'}
	 	        ]
	    });
		
		var detailGrid=Ext.create('Ext.grid.Panel', {
			anchor:'100% 100%',
	        store: detailGridStore,
	        columns: [ {
	            text: '所属机构',
	            flex:1,
	            dataIndex: 'branchname',
	            sortable: true,
	            border:true
	        },{
	            text: '客户账号',
	            width: 200,
	            dataIndex: 'accountid',
	            sortable: true,
	            border:true
	        },{
	            text: '客户名称',
	            width: 200,
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
	        }],
	        dockedItems : [{
	       				xtype:'toolbar',
	    				dock:'top',
	    				items:['->',{
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
			title:'个人质押存款统计',
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