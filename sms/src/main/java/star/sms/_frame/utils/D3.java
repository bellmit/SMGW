package star.sms._frame.utils;

import java.util.List;

/**
 * @author star
 */
public class D3 {
	
	private String id;
	private String name;
	private String image;
	private Integer width;
	private Integer height;
	private Integer size;
	/**
	 * 父节点连接线标书
	 */
	private String parentlinename;
	/**
	 * 子节点
	 */
	private List<D3> children;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getParentlinename() {
		return parentlinename;
	}
	public void setParentlinename(String parentlinename) {
		this.parentlinename = parentlinename;
	}
	public List<D3> getChildren() {
		return children;
	}
	public void setChildren(List<D3> children) {
		this.children = children;
	}
	
}
