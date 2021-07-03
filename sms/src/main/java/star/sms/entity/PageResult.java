package star.sms.entity;

import java.util.List;

/**
 * @author star
 */
public class PageResult<T> {

	private Long total;
	private List<T> rows;

	private List<T> allRows;
	
	public PageResult(Long total, List<T> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}

	public PageResult(Long total, List<T> rows, List<T> allRows) {
		this.total = total;
		this.rows = rows;
		this.allRows = allRows;
	}

	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public List<T> getAllRows() {
		return allRows;
	}

	public void setAllRows(List<T> allRows) {
		this.allRows = allRows;
	}
}
