function updateCheckBoxTreeStatus(node,checked)
{
			function updateChildNode(node,checked)
			{
				node.checked = checked; 
				//如果不是叶子节点则需递归更新子节点状态
				if(!node.isLeaf())
				{  
		            node.eachChild(function(childnode)
		            {  
		            	childnode.set('checked', checked); 
		            	childnode.checked=checked;
		            	if(childnode.hasChildNodes())
		            	{
		            		updateChildNode(childnode,checked);
		    			} 
		            })  
		        }
			}
			function haveCheckedChildNode(node)
			{
				
				var find=false;
				
					node.eachChild(function(childnode)
					{  
						//如果子节点为选中状态则返回true
						if(childnode.get('checked'))
						{
							find=true;
							return false;
						}
					}
					)
				return find;
				  
			}
			//如果不是root节点同时需要对父节点进行更新，如果父节点下没有选择的节点则需要设置状态为false
			function updateParentNode(node)
			{
				if(!node.isRoot())
				{
					var parentNode=node.parentNode;
					if(haveCheckedChildNode(parentNode))
					{
						parentNode.set('checked', true); 
						parentNode.checked=true;
					}
					else
					{
						parentNode.set('checked', false); 
						parentNode.checked=false;
					}
					updateParentNode(parentNode);
				}
				else
				{
					var rtn=haveCheckedChildNode(node);
					if(rtn)
					{
						node.set('checked', true); 
						node.checked=true;
					}
					else
					{
						node.set('checked', false);
						node.checked=false;
					}
				}
			}
			
			updateChildNode(node,checked);
			updateParentNode(node);
			
}