package star.sms._frame.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 人员账户和卡号信息
 */
public class AccountNode {
	private String nodeId;// 树的节点Id，区别于数据库中保存的数据Id。
	private String text; // 节点名称
	private String icon;
	private String[] tags = new String[] { "1" };
	private Boolean selectable;
	private Integer nodeLeve;
	private List<AccountNode> nodes; // 子节点，可以用递归的方法读取
	private State state = new State();
	private String accountBody;
	private String accountBodyCard;
	public class State{
		//是否默认展开节点
		private Boolean expanded = true;
		private Boolean selecttable = true;
		
		public Boolean getExpanded() {
			return expanded;
		}
		public void setExpanded(Boolean expanded) {
			this.expanded = expanded;
		}
		public Boolean getSelecttable() {
			return selecttable;
		}
		public void setSelecttable(Boolean selecttable) {
			this.selecttable = selecttable;
		}
		
	}
	public AccountNode() {
		this.nodes = new ArrayList<AccountNode>();
	}

	public AccountNode(String nodeId, String pid) {
		this.nodeId = nodeId;
		this.nodes = new ArrayList<AccountNode>();
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<AccountNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<AccountNode> nodes) {
		this.nodes = nodes;
	}

	public Boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(Boolean selectable) {
		this.selectable = selectable;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public Integer getNodeLeve() {
		return nodeLeve;
	}

	public void setNodeLeve(Integer nodeLeve) {
		this.nodeLeve = nodeLeve;
	}
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getAccountBody() {
		return accountBody;
	}

	public void setAccountBody(String accountBody) {	
		this.accountBody = accountBody;
	}

	public String getAccountBodyCard() {
		return accountBodyCard;
	}

	public void setAccountBodyCard(String accountBodyCard) {
		this.accountBodyCard = accountBodyCard;
	}
}