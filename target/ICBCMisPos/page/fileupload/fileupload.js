
Ext.application({
	name : '文件上传',
	launch : function() {
		var uploadForm=Ext.create('Ext.form.Panel',{
			bodyPadding: '10 10 0',
			anchor:'100% 25%',
			name:'上传文件',
			
			layout:'form',
			border:true,
			items:[{
				xtype:'panel',
				layout:'column',
				border:false,
				items:[{
					columnWidth:0.5,
					xtype: 'filefield',
			        name: 'file',
			        fieldLabel: 'file',
			        labelWidth: 50,
			        msgTarget: 'side',
			        allowBlank: false,
			        buttonText: '选择文件...'
				}]
			}],
			buttonAlign:'left',
			buttons:[{
				text:'上传文件',
				handler:function(){
					if(uploadForm.getForm().isValid()){
						uploadForm.getForm().submit({
							url:'../../action/upload',
							waitMsg:'正在上传文件。。。。',
							success:function(p,o){
								alert('success');
							}
						})
					}
				}
			}]
		});
		var gridStore = Ext.create('Ext.data.Store', {
			autoLoad : true,
			fields : [ 'clinicdate', 'clinicfee'],
			pageSize:1,
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
		var grid=Ext.create('Ext.grid.Panel',{
			store:gridStore,
			selType: 'cellmodel',
		    plugins: [
		        Ext.create('Ext.grid.plugin.RowEditing', {
		            clicksToEdit: 1,
		            saveBtnText:'保存'
		        })
		    ],
			columns:[{
				header:'姓名',
				dataIndex:'clinicdate',
				field:'textfield'
			},{
				header:'占比',
				dataIndex:'clinicfee',
				editor:'datefield'
			},{
				header:'删除',
				xtype:'actioncolumn',
				items:[{
						tooltip:'del',
						handler:function(){
							alert('del');
						}
				}]
			}],
			dockedItems: [{
		        xtype: 'toolbar',
		        dock: 'top',
		        items: [{
		            text: 'add',
		            handler:function(){
		            	alert(gridStore.getCount())
		            	gridStore.insert(gridStore.getCount(),[{'clinicdate':'','clinicfee':''}]);
		            }
		        }]
		    }]
		})
		var tableForm=Ext.create('Ext.form.Panel',{
			anchor:'100% 30%',
			name:'表格编辑',
			layout:'fit',
			items:[grid]
		})
		//角色信息列表显示
		var queryPanel = Ext.create('Ext.panel.Panel', {
			name:'queryPanel',
			title : '文件上传',
			layout : {
				type:'anchor'
			},
			items : [uploadForm]
		});
		

		
		Ext.create('Ext.container.Viewport', {
			layout :'fit',
			items : [ queryPanel],
			renderTo : Ext.getBody()
			
		})
	}
})