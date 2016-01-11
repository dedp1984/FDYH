
Ext.application({
	name : 'MisPos交易明细查询',
	launch : function() {
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'clinicdate', 'clinicfee','devdate' ,'devno','pospcode','tranacct','trancode','pospcodecomment','rcptno','indexno'],
			pageSize:10,
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
		            root:'items',
		            totalProperty: 'totalSize'
		        },
				url : '../../action/journal/queryElectricJournalList'
			}
		});
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 75%',
			store : gridStore,
			flex:2,
			columns : [ Ext.create('Ext.grid.RowNumberer', {
				width : 30
			}),{
				header : '交易类型',
				dataIndex : 'trancode',
				width:100,
				renderer:function(value){
					if(value=='0200000000')
						return '消费';
					else if(value=='0420000000')
						return '冲正';
					else if(value=='0400000000')
						return '当日撤销';
					else if(value=='0405170000')
						return '隔日退货';
					else if(value=='0800900010')
						return '签到 ';
					else if(value=='0150310000')
						return '查询余额';
					else 
						return '其他交易';
				}
			}, {
				header : '交易日期',
				dataIndex : 'devdate'
			}, {
				header : '交易账号',
				flex:1,
				dataIndex : 'tranacct'
			},  {
				header : '交易金额',
				dataIndex : 'clinicfee',
				width:100
			}, {
				header : '结果',
				dataIndex : 'pospcode',
				width:100
			},{
				header : '描述',
				dataIndex : 'pospcodecomment',
				width:100
			},{
				header : '终端号',
				dataIndex : 'devno',
				width:100
			},{
				header : '终端流水号',
				dataIndex : 'rcptno',
				width:100
			},{
				header : '检索参考号',
				dataIndex : 'indexno',
				width:100
			}],
			dockedItems : [ {
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true
			} ]

		});
		
		var queryForm=Ext.create('Ext.form.Panel',{
			border:1,
			bodyPadding:'20 20 0',
			anchor:'100% 25%',
			paramsAsHash: true,
			layout : 'form',
			items : [{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'combobox',
						fieldLabel : '交易类型',
						name:'trancode',
						displayField:'tranname',  
				        valueField:'trancode',
				        store:{  
				            type:'array',  
				            fields:["trancode","tranname"],  
				            data:[  
				                ['0200000000','消费'],  
				                ['0420000000','冲正'],
				                ['0400000000','当日撤销'],
				                ['0405170000','隔日退货'],
				                ['0800900010','签到'],
				                ['0150310000','查询余额']
				            ] 
				        }
						}]
				},{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'datefield',
						fieldLabel : '交易日期',
						name:'devdate',
						format:'Ymd'
						}]
				},{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '交易账号',
						name:'tranacct'
						}]
				}]
			} ,{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.3,  //该列占用的宽度，标识为50％
					layout:'hbox',
					border:false,
					items:[{
						xtype:'textfield',
						fieldLabel : '交易金额',
						name:'clinicfee'
						}]
				}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				handler:function(){
					
					if(queryForm.getForm().isValid()){
						var trancode = queryForm.getForm().findField('trancode').getValue();
						var devdate = Ext.util.Format.date(queryForm.getForm().findField('devdate').getValue(),'Ymd');
						var tranacct = queryForm.getForm().findField('tranacct').getValue();
						var clinicfee = queryForm.getForm().findField('clinicfee').getValue();
						gridStore.currentPage=1;
						gridStore.load({
						    params:{
						        start:0,
						        limit: 10,
						        page:1,
						        'trancode' : trancode,
								'devdate':devdate,
								'tranacct':tranacct,
								'clinicfee':clinicfee
						    }
						});
					}
				}
			},{
				text:'重置',
				handler:function(){
					queryForm.getForm().reset();
				}
			}]
		});
		//角色信息列表显示
		var queryPanel = Ext.create('Ext.panel.Panel', {
			name:'queryPanel',
			title : 'MisPos交易明细查询',
			layout : {
				type:'anchor'
			},
			items : [queryForm,queryGrid]
		});
		

		
		Ext.create('Ext.container.Viewport', {
			layout :'fit',
			items : [ queryPanel],
			renderTo : Ext.getBody()
			
		})
	}
})