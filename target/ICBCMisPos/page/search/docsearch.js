
Ext.application({
	name : '文档检索',
	launch : function() {

		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : false,
			fields : [ 'fileName', 'path','preview'],
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
				url : '../../action/search/docSearchByKeyWord'
			}
		});
		 function renderFileUrl(value, p, record) {
		        return Ext.String.format(
		            '<b><a href="../../upload/{0}" target="_blank">{1}</a></b>',
		            record.get('fileName'),
		            record.get('fileName')
		        );
		    }
		var pluginExpanded = true;
		var queryGrid = Ext.create('Ext.grid.Panel', {
			anchor:'100% 75%',
			store : gridStore,
			flex:2,
			disableSelection: true,
	        loadMask: true,
			viewConfig : {
				id : 'gv',
				trackOver : false,
				stripeRows : false,
				plugins : [ {
					ptype : 'preview',
					bodyField : 'preview',
					expanded : true,
					pluginId : 'preview'
				} ]
			},
			columns : [{
				header : '文件名称',
				dataIndex : 'fileName',
				flex:1,
				renderer:renderFileUrl
			}, {
				header : '文件路径',
				dataIndex : 'path',
				width:200
			}],
			dockedItems : [ {
				xtype : 'pagingtoolbar',
				store : gridStore, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true,
				items:[
		                '-', {
		                text: 'Show Preview',
		                pressed: pluginExpanded,
		                enableToggle: true,
		                toggleHandler: function(btn, pressed) {
		                    var preview = Ext.getCmp('gv').getPlugin('preview');
		                    preview.toggleExpanded(pressed);
		                }
		            }]
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
					columnWidth:0.5,  //该列占用的宽度，标识为50％
					//layout:'hbox',
					border:false,
					xtype:'textfield',
					allowBlank:false,
					fieldLabel : '查询关键字',
					name:'keyWord'/*
					items:[{
						xtype:'textfield',
						fieldLabel : '查询关键字',
						name:'keyWord',
						align:'middle',
						flex:1
						}]*/
				}]
			}],
			buttonAlign:'center',
			buttons:[{
				text:'查询',
				handler:function(){
					
					if(queryForm.getForm().isValid()){
						var keyWord = queryForm.getForm().findField('keyWord').getValue();
						gridStore.currentPage=1;
						gridStore.load({
						    params:{
						        start:0,
						        limit: 10,
						        page:1,
						        'keyWord' : keyWord
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
			title : '文档检索',
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