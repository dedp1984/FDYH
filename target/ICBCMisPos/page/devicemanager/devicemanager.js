Ext.application({
	name : '门禁设备信息管理',
	launch : function() {
		
		var treeNode;
		var editFlag;
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
		            {name:'parentid',type:'string'},
		            {name:'devId',type:'string'},
		            {name:'devName',type:'string'},
		            {name:'areaId',type:'string'},
		            {name:'areaName',type:'string'},
		            {name:'entryId',type:'string'},
		            {name:'entryName',type:'string'},
		            {name:'entryType',type:'string'},
		            {name:'nodeType',type:'string'},
		            {name:'floorCnt',type:'string'},
		            {name:'floorRoomCnt',type:'string'},
		            {name:'adrZoneId',type:'string'},
		            {name:'iosZoneId',type:'string'},
		            {name:'devAddress',type:'string'}
		            ],
		   
		     proxy: {
						type:'ajax',
						actionMethods:{
							create: 'POST', 
							read: 'POST', 
							update: 'POST', 
							destroy: 'POST'
						},
						reader: {  
			                    type:'json'
			            },
						url:'../../action/areamanager/queryAreaTree'
					}
		});
		var addEntryAction=Ext.create('Ext.Action',{
			text:'增加小区入口',
			icon:'../../images/add.gif',
			handler:function()
			{
				editFlag=false;
				editPanel.removeAll(false);
				editPanel.insert(0,editEntryForm);
				editPanel.setDisabled(false);
				editPanel.setVisible(true);
				editEntryForm.setTitle("增加小区入口");
				editEntryForm.getForm().reset();
				editEntryForm.getForm().findField('areaid').setValue(treeNode.get('id'));
				editEntryForm.getForm().findField('areaname').setReadOnly(true);
				editEntryForm.getForm().findField('areaname').setValue(treeNode.get('text'));
				editEntryForm.getForm().url='../../action/entrymanager/addEntry';
			}
		});
		var delEntryAction=Ext.create('Ext.Action',{
			text:'删除小区入口',
			icon:'../../images/del.gif',
			handler:function()
			{
				Ext.Msg.show({
					msg:'确定删除【'+treeNode.get('text')+'】?', 
					title:'提示信息', 
					buttons:Ext.Msg.OKCANCEL,
					fn:function(btn, text){
						if (btn == 'ok'){
					    	var request=Ext.Ajax.request({
								params: {
									areaid:treeNode.get('areaId'),
							        entryid: treeNode.get('entryId')
							    },
								url:'../../action/entrymanager/deleteEntry',
								method:'GET',
								success:function(response,options){
									alert('删除小区入口成功');
									treeStore.load();
								},
							    failure:function(response,options){
							    	alert('删除小区入口失败');
							    }
							});
					    }
					}
				})
			}
					
		});
		var editEntryAction=Ext.create('Ext.Action',{
			text:'修改小区入口',
			icon:'../../images/modify.gif',
			handler:function(){
				editFlag=true;
				editPanel.removeAll(false);
				editPanel.insert(0,editEntryForm);
				editPanel.setDisabled(false);
				editPanel.setVisible(true);
				editEntryForm.setTitle("修改小区入口");
				editEntryForm.getForm().findField('areaid').setValue(treeNode.get('areaId'));
				editEntryForm.getForm().findField('areaname').setReadOnly(true);
				editEntryForm.getForm().findField('areaname').setValue(treeNode.get('areaName'));
				editEntryForm.getForm().findField('entryid').setValue(treeNode.get('entryId'));
				editEntryForm.getForm().findField('entryname').setValue(treeNode.get('entryName'));
				editEntryForm.getForm().findField('entrytype').setValue(treeNode.get('entryType'));
				if(treeNode.get('entryType')=='1'){
					editEntryForm.getForm().findField('floorcnt').setDisabled(true);
					editEntryForm.getForm().findField('floorroomcnt').setDisabled(true);
					editEntryForm.getForm().findField('floorcnt').setVisible(false);
					editEntryForm.getForm().findField('floorroomcnt').setVisible(false);
				}else{
					editEntryForm.getForm().findField('floorcnt').setDisabled(false);
					editEntryForm.getForm().findField('floorroomcnt').setDisabled(false);
					editEntryForm.getForm().findField('floorcnt').setVisible(true);
					editEntryForm.getForm().findField('floorroomcnt').setVisible(true);
					editEntryForm.getForm().findField('floorcnt').setValue(treeNode.get('floorCnt'));
					editEntryForm.getForm().findField('floorroomcnt').setValue(treeNode.get('floorRoomCnt'));
				}
				
				editEntryForm.getForm().url='../../action/entrymanager/modifyEntry';
			}
		});
		
		var addDeviceAction=Ext.create('Ext.Action',{
			text:'增加门禁设备',
			icon:'../../images/add.gif',
			handler:function()
			{
				editFlag=false;
				editPanel.removeAll(false);
				editPanel.insert(0,editDeviceForm);
				editPanel.setDisabled(false);
				editPanel.setVisible(true);
				editDeviceForm.setTitle("增加门禁设备信息");
				editDeviceForm.getForm().reset();
				editDeviceForm.getForm().findField('areaid').setValue(treeNode.get('areaId'));
				editDeviceForm.getForm().findField('areaname').setReadOnly(true);
				editDeviceForm.getForm().findField('areaname').setValue(treeNode.get('areaName'));
				editDeviceForm.getForm().findField('entryid').setValue(treeNode.get('entryId'));
				editDeviceForm.getForm().findField('entryname').setReadOnly(true);
				editDeviceForm.getForm().findField('entryname').setValue(treeNode.get('entryName'));
				editDeviceForm.getForm().findField('entrytype').setValue(treeNode.get('entryType'));
				editDeviceForm.getForm().url='../../action/devicemanager/addDevice';
			}
		});
		var delDeviceAction=Ext.create('Ext.Action',{
			text:'删除门禁设备',
			icon:'../../images/del.gif',
			handler:function()
			{
				Ext.Msg.show({
					msg:'确定删除设备【'+treeNode.get('text')+'】?', 
					title:'提示信息', 
					buttons:Ext.Msg.OKCANCEL,
					fn:function(btn, text){
						if (btn == 'ok'){
					    	var request=Ext.Ajax.request({
								params: {
							        'devid': treeNode.get('devId'),
							        'areaid':treeNode.get('areaId'),
							        'entryid':treeNode.get('entryId')
							    },
								url:'../../action/devicemanager/deleteDevice',
								method:'GET',
								success:function(response,options){
									alert('删除设备信息成功');
									treeStore.load();
								},
							    failure:function(response,options){
									alert('删除设备信息失败');
								}
							});
					    }
					}
				})
			}
					
		});
		var editDeviceAction=Ext.create('Ext.Action',{
			text:'修改门禁设备',
			icon:'../../images/modify.gif',
			handler:function(){
				editFlag=true;
				editPanel.removeAll(false);
				editPanel.insert(0,editDeviceForm);
				editPanel.setDisabled(false);
				editPanel.setVisible(true);
				editDeviceForm.setTitle("修改门禁设备信息");
				editDeviceForm.getForm().findField('areaid').setValue(treeNode.get('areaId'));
				editDeviceForm.getForm().findField('areaname').setReadOnly(true);
				editDeviceForm.getForm().findField('areaname').setValue(treeNode.get('areaName'));
				editDeviceForm.getForm().findField('entryid').setValue(treeNode.get('entryId'));
				editDeviceForm.getForm().findField('entryname').setReadOnly(true);
				editDeviceForm.getForm().findField('entryname').setValue(treeNode.get('entryName'));
				editDeviceForm.getForm().findField('entrytype').setReadOnly(true);
				editDeviceForm.getForm().findField('entrytype').setValue('1');
				editDeviceForm.getForm().findField('devname').setValue(treeNode.get('devName'));
				editDeviceForm.getForm().findField('devid').setValue(treeNode.get('devId'));
				editDeviceForm.getForm().findField('devaddress').setValue(treeNode.get('devAddress'));
				editDeviceForm.getForm().findField('adrzoneid').setValue(treeNode.get('adrZoneId'));
				editDeviceForm.getForm().findField('ioszoneid').setValue(treeNode.get('iosZoneId'));
				editDeviceForm.getForm().url='../../action/devicemanager/modifyDevice';
			}
		});
		
		
	    var contextMenu = Ext.create('Ext.menu.Menu', {
		       
		    });
		var treePanel = Ext.create('Ext.tree.Panel', {
			title: '设备信息管理',
			flex:4,
			width:400,
			bodyPadding:'0 0 0 5',
			store:treeStore,
			rootVisible: false,
		    aoutLoad:true,
			listeners:{
				itemcontextmenu: function(view, rec, node, index, e) {
					e.stopEvent();
					contextMenu.removeAll(false);
					if(rec.get('nodeType')=="1"){
						 contextMenu.insert(0,addEntryAction);
					}
					else if(rec.get('nodeType')=="2"){
						 contextMenu.insert(0,addDeviceAction);
						 contextMenu.insert(1,editEntryAction);	
						 contextMenu.insert(2,delEntryAction);
					}
					else if(rec.get('nodeType')=="3"){
						contextMenu.insert(0,editDeviceAction);
						contextMenu.insert(1,delDeviceAction);
					}
					contextMenu.showAt(e.getXY());
					treeNode=rec;
	                return false;
                }
			}
		});
		
		var editEntryForm = Ext.create('Ext.form.Panel',{
			title:'编辑入口信息',
			height:300,
			bodyPadding:'0 0 0 5',
			border:true,
			items:[{
				xtype:'textfield',
				name:'areaid',
				fieldLabel:'小区ID',
				hidden:true
			},{
				xtype:'textfield',
				name:'areaname',
				fieldLabel:'所属小区'
			},{
				xtype:'textfield',
				name:'entryid',
				fieldLabel:'入口id',
				hidden:true
			},{
				xtype:'textfield',
				name:'entryname',
				fieldLabel:'入口名称',
				allowBlank:false
			},{
				xtype:'combobox',
				fieldLabel : '入口类型',
				name:'entrytype',
				displayField:'entrytypename',  
		        valueField:'entrytype',
		        forceSelection: true,
		        editable:false,
		        allowBlank:false,
		        value:'0',
		        store:{  
		            type:'array',  
		            fields:["entrytype","entrytypename"],  
		            data:[  
		                ['0','住宅'],  
		                ['1','大门']
		            ] 
		        },
		        listeners:{
		        	afterrender:function(combo){
		        		if(combo.getValue()=='1'){
		        			editEntryForm.getForm().findField('floorcnt').setDisabled(true);
		        			editEntryForm.getForm().findField('floorroomcnt').setDisabled(true);
		        		}else{
		        			editEntryForm.getForm().findField('floorcnt').setDisabled(false);
		        			editEntryForm.getForm().findField('floorroomcnt').setDisabled(false);
		        		}
		        	},
		        	select:function(combo,records,opts){
		        		if(combo.getValue()=='1'){
		        			editEntryForm.getForm().findField('floorcnt').setDisabled(true);
		        			editEntryForm.getForm().findField('floorroomcnt').setDisabled(true);
							editEntryForm.getForm().findField('floorcnt').setVisible(false);
							editEntryForm.getForm().findField('floorroomcnt').setVisible(false);
		        		}else{
		        			editEntryForm.getForm().findField('floorcnt').setDisabled(false);
		        			editEntryForm.getForm().findField('floorroomcnt').setDisabled(false);
							editEntryForm.getForm().findField('floorcnt').setVisible(true);
							editEntryForm.getForm().findField('floorroomcnt').setVisible(true);
		        		}
		        	}
		        }
			 },{
				 	xtype:'numberfield',
					name: 'floorcnt',
			        fieldLabel: '楼层数',
			        value: 1,
			        maxValue: 99,
			        minValue: 1
			},{
				xtype:'numberfield',
				name: 'floorroomcnt',
		        fieldLabel: '每层户数',
		        value: 1,
		        maxValue: 99,
		        minValue: 1
			}],
			buttonAlign:'center',
			buttons:[{
				text:'保存',
				glyph:'xf0c7@FontAwesome',
				handler:function(){
					if(editEntryForm.getForm().isValid()){
						editEntryForm.getForm().submit({
							success:function(form,action){
								alert(editFlag==false?'增加入口信息成功':'修改入口信息成功');
								editEntryForm.getForm().reset();
								editPanel.setDisabled(true);
								editPanel.setVisible(false);
								treePanel.getStore().load();
							},
							failure:function(form,action){
								alert(editFlag==false?'增加入口信息失败':'修改入口信息失败');
							}
						})
					}
				}
			},{
				text:'返回',
				glyph:'xf0e2@FontAwesome',
				handler:function(){
					editEntryForm.getForm().reset();
					editPanel.setDisabled(true);
					editPanel.setVisible(false);
				}
			}]
		})
		var editDeviceForm = Ext.create('Ext.form.Panel',{
			title:'编辑设备信息',
			height:250,
			bodyPadding:'0 0 0 5',
			border:true,
			items:[{
				xtype:'textfield',
				name:'areaid',
				fieldLabel:'小区ID',
				hidden:true
			},{
				xtype:'textfield',
				name:'areaname',
				fieldLabel:'所属小区'
			},{
				xtype:'textfield',
				name:'entryid',
				fieldLabel:'入口id',
				hidden:true
			},{
				xtype:'textfield',
				name:'entryname',
				fieldLabel:'入口名称',
				allowBlank:false
			},{
				xtype:'combobox',
				fieldLabel : '入口类型',
				name:'entrytype',
				displayField:'entrytypename',  
		        valueField:'entrytype',
		        forceSelection: true,
		        editable:false,
		        allowBlank:false,
		        value:'0',
		        store:{  
		            type:'array',  
		            fields:["entrytype","entrytypename"],  
		            data:[  
		                ['0','住宅'],  
		                ['1','大门']
		            ] 
		        }
			 },{
					xtype:'textfield',
					name:'devname',
					fieldLabel:'设备名称',
					allowBlank:false
			},{
				xtype:'textfield',
				name:'devaddress',
				fieldLabel:'蓝牙设备地址',
			},{
				xtype:'textfield',
				name:'adrzoneid',
				fieldLabel:'ZONEID',
			},{
				xtype:'textfield',
				name:'ioszoneid',
				fieldLabel:'ISO系统设备ID',
				hidden:true
			},{
				xtype:'textfield',
				name:'devid',
				fieldLabel:'设备ID',
				hidden:true
			}],
			buttonAlign:'center',
			buttons:[{
				text:'保存',
				glyph:'xf0c7@FontAwesome',
				handler:function(){
					if(editDeviceForm.getForm().isValid()){
						editDeviceForm.getForm().submit({
							success:function(form,action){
								alert(editFlag==false?'增加设备信息成功':'修改设备信息成功');
								editDeviceForm.getForm().reset();
								editPanel.setDisabled(true);
								editPanel.setVisible(false);
								treePanel.getStore().load();
							},
							failure:function(form,action){
								alert(editFlag==false?'增加设备信息失败':'修改设备信息失败');
							}
						})
					}
				}
			},{
				text:'返回',
				glyph:'xf0e2@FontAwesome',
				handler:function(){
					editDeviceForm.getForm().reset();
					editPanel.setDisabled(true);
					editPanel.setVisible(false);
				}
			}]
		})
		
		var editPanel=Ext.create('Ext.panel.Panel',{
			xtype:'panel',
			flex:2,
			disabled:true,
		    rootVisible:false,
		    hidden:true,
			items:[]
		});
		
		var branchTreeStore= Ext.create('Ext.data.TreeStore', {
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
			store:branchTreeStore,
			rootVisible: false,
		    aoutLoad:true,
			listeners:{
                itemclick:function(view, record, item, index, e, eOpts){
                	treeStore.getProxy().extraParams={
                		branchId:record.get('id')
                	},
                	treeStore.load();
                }
			}
		});
		var devicePanel=Ext.create('Ext.panel.Panel',{
			flex:3,
			layout:{
				type:'hbox',
				align:'stretch'
			},
			bodyPadding: '0 0 0 5',
			border:true,
			items : [treePanel,editPanel]
		})
		var viewPort=Ext.create('Ext.container.Viewport', {
			layout:{
				type:'hbox',
				align:'stretch'
			},
			items : [branchPanel,devicePanel],
			renderTo : Ext.getBody()
		});
		
		
	}
});