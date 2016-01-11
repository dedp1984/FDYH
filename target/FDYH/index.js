Ext.Loader.setConfig({
	enabled : true
});

//功能导航栏
var accord=Ext.create('Ext.panel.Panel', {
    width: 300,
    height: 300,
    defaults: {
        // applied to each contained panel
        bodyStyle: 'padding:15px'
    },
    layout: {
        // layout-specific configs go here
        type: 'accordion',
        titleCollapse: false,
        animate: true,
        activeOnTop: false
    }});
Ext.application({
	name : '主界面',
	launch : function() {
		var account;
		
		/** 
		 * 定义了一个背景透明的Button类，继承于Button 
		 */  
		Ext.define('app.ux.ButtonTransparent', {  
		            extend : 'Ext.button.Button', // 继续于Ext.button.Button  
		            alias : 'widget.buttontransparent', // 此类的xtype类型为buttontransparent  
		            // 类初始化时执行  
		            initComponent : function() {  
		                // 设置事件监听  
		                this.listeners = {  
		                    // 鼠标移开，背景设置透明  
		                    mouseout : function() {  
		                        this.setTransparent(document.getElementById(this.id));  
		                    },  
		                    // 鼠标移过，背景取消透明  
		                    mouseover : function() {  
		                        var b = document.getElementById(this.id);  
		                        b.style.backgroundImage = '';  
		                        b.style.backgroundColor = '';  
		                        b.style.borderColor = '';  
		                    },  
		                    // 背景设置透明  
		                    afterrender : function() {  
		                        this.setTransparent(document.getElementById(this.id));  
		                    }  
		                };  
		  
		                this.callParent(arguments); // 调用你模块的initComponent函数  
		            },  
		  
		            setTransparent : function(b) {  
		                b.style.backgroundImage = 'inherit';  
		                b.style.backgroundColor = 'inherit';  
		                b.style.borderColor = 'transparent';  
		            }  
		        });  
		
		var editAccount=Ext.create('Ext.window.Window', {
		    title: '修改密码',
		    border:true,
		    frame:false,
		    closable:false,

		    layout: 'hbox',
		    items: {  // Let's put an empty grid in just to illustrate fit layout
		        xtype: 'form',
		        height: 200,
			    width: 300,
			    id:'editAccountForm',
		        border: false,
		        layout:'form',
		        closeAction: 'hidden',
		        url:'action/account/modifyAccountPassword',
		        items:[{
		        	xtype:'textfield',
		        	name:'accountid',
		        	id:'accountid',
		        	fieldLabel:'用户ID',
		        	labelAlign:'right',
		        	labelWidth:80,
		        	readOnly:true
		        },{
		        	xtype:'textfield',
		        	name:'accountname',
		        	id:'accountname',
		        	fieldLabel:'用户姓名',
		        	labelAlign:'right',
		        	labelWidth:80,
		        	readOnly:true
		        },{
		        	xtype:'textfield',
		        	name:'oldpassword',
		        	fieldLabel:'原密码',
		        	labelAlign:'right',
		        	labelWidth:80,
		        	inputType:'password',
		        	allowBlank:false
		        },{
		        	xtype:'textfield',
		        	name:'newpassword',
		        	id:'newpassword',
		        	labelWidth:80,
		        	labelAlign:'right',
		        	fieldLabel:'新密码',
		        	inputType:'password',
		        	allowBlank:false
		        },{
		        	xtype:'textfield',
		        	name:'newpassword1',
		        	id:'newpassword1',
		        	labelWidth:80,
		        	labelAlign:'right',
		        	fieldLabel:'新密码',
		        	inputType:'password',
		        	allowBlank:false
		        }],
		        buttons:[{
		    		text:'保存',
		    		width:80,
		    		glyph:'xf0c7@FontAwesome',
		    		handler:function(){
		    			if(Ext.getCmp('editAccountForm').getForm().isValid()){
		    				var newpassword=Ext.getCmp('newpassword').getValue();
		    				var newpassword1=Ext.getCmp('newpassword1').getValue();
		    				if(newpassword!=newpassword1){
		    					Ext.Msg.alert('提示信息','两次输入密码不一致');
		    					Ext.getCmp('newpassword').setValue('');
		    					Ext.getCmp('newpassword').setValue('');
		    					
		    					return;
		    				}
		    				Ext.getCmp('editAccountForm').getForm().submit({
		    					waitMsg:'正在处理中',
		    					success:function(response,options){
		    						Ext.Msg.alert('提示信息','修改密码成功');
		    						window.location = 'login.html';
		    						
		    					},
		    					failure:function(form,action){
		    						Ext.Msg.alert('提示信息',action.result.errors.errmsg);
		    					}
		    				});
		    			}
		    		}
		    	},{
		    		text:'取消',
		    		width:80,
		    		glyph:'xf0e2@FontAwesome',
		    		handler:function(){
		    			editAccount.hide();
		    		}
		    	}]
		    },
		listeners:{
    		afterlayout:function(){
    			Ext.getCmp('accountid').setValue(account.items.accountid);
    			Ext.getCmp('accountname').setValue(account.items.accountname);
    		}
    	}
		});
		Ext.create('Ext.container.Viewport', {
			title : 'Border Layout',
			layout : 'border',
			items : [ {
				region : 'north', // position for region
				margins : '0 0 1 0',
				items:[{
					xtype:'toolbar',
					height:60,
					
					style:'background-color:#438EB9;', 
					items:[
					        // add a vertical separator bar between toolbar items
					        {
					        	xtype:'label',
					        	text:'富滇银行重庆分行客户经理绩效考核系统',
					        	style:'font-size:20px;color:#000000;font-weight:bold;font-family:SimHei' 
					        },
					        '->',
					        {
					        	xtype:'tbtext',
					        	id:'welcome',
					        	style:'font-size:15px;color:#000000;font-weight:bold;'
					        },
					        '-',
					        {
					        	xtype:'tbtext',
					        	id:'branchname',
					        	style:'font-size:15px;color:#000000;font-weight:bold;'
					        },
					        { xtype: 'tbspacer', width: 50 }, // add a 50px space
					        {
					        	xtype:'button',
					        	text:'修改密码',
					        	glyph:'xf007@FontAwesome',
					        	style:'font-size:20px;',
					        	handler:function(){
					        		editAccount.show();
					        	}
					        },
					        {
					        	text:'退出系统',
					        	xtype:'button',
					        	glyph:'xf011@FontAwesome',
					        	handler:function(){
					        		Ext.Msg.show({
										msg:'确定退出系统?', 
										title:'信息提示', 
										buttons:Ext.Msg.OKCANCEL,
										fn:function(btn, text){
											if (btn == 'ok'){
										    	var request=Ext.Ajax.request({
													url:'action/logout',
													success:function(response,options){
														window.location = 'login.html' 
													}
												});
										    }
										}
									})
					        	}
					        }
					    ]
				}]
			}, {
				region : 'south', // position for region
				hidden:true,
				xtype : 'panel',
				height : 30,
				split : false,
				margins : '1 0 0 0'
			// enable resizing
			}, {
				// xtype: 'panel' implied by default
				title : '功能导航',
				region : 'west',
				xtype : 'panel',
				border:true,
				width : 250,
				
				collapsible : true, // make collapsible
				id : 'west-region-container',
				layout : 'fit',
				items :[accord]
			}, {
				region:'center',
				layout:'border',
				margins : '0 0 0 1',
				border:false,
				items:[
					{
						layout:'fit',
						region:'center',
						border:false,
						id:'FD',
						contentEl:'fCenter'
					}
				]
			} ],
			renderTo : Ext.getBody(),
			listeners:{
				afterrender:function(){
					var request=Ext.Ajax.request({
						url:'ajax/account/queryAccount',
						method:'GET',
						success:function(response,options){
							account=Ext.JSON.decode(response.responseText,false);
							Ext.getCmp('welcome').setText('姓名：'+account.items.accountname+'');
							Ext.getCmp('branchname').setText('所属机构：'+account.items.branch.branchname+'');
						}
					});
				}
			}
		});
		
		
		var request=Ext.Ajax.request({
			params: {
		        id: '0'
		    },
			url:'ajax/menu/queryTopMenuByAccountId',
			method:'GET',
			success:update
		});
		
		function leafItemClick(view, record, item, index,  e, eOpts)
		{
			if(record.get('leaf')==true)
			{
					document.getElementById('fCenter').src=record.get('url');
			}
			
		}
		
		function update(response,options)
		{
			var treeArray=Ext.JSON.decode(response.responseText,false);
			for(var i=0;i<treeArray.length;i++){
				var treeStore= Ext.create('Ext.data.TreeStore', {
				    root: {
				        expanded: true
				    },
				    nodeParam: 'id',
				    defaultRootId:treeArray[i].id,
				    
				    fields:[{name:'id',type:'string'},
				            {name:'text',type:'string'},
				            {name:'leaf',type:'boolean'},
				            {name:'url',type:'string'}
				            
				            ],
					proxy: {
						type:'ajax',
						url:'action/menu/querySubMenuByAccountId'
					}
				});
				var menuTree = Ext.create('Ext.tree.Panel', {
					title: treeArray[i].text,
					store:treeStore,
					width: 200,
				    height: 150,
					rootVisible: false,
					listeners:{
						itemclick:{
							fn:leafItemClick
						}
					}
				});
				accord.insert(i,menuTree);
				}
		}
		
		
	}
});