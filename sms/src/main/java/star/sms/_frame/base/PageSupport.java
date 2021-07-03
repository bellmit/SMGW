package star.sms._frame.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author star
 */
public class PageSupport {

	private int pageNumber = 0 ;
	private int pageSize = 10;
	
	public Pageable getPage(){
		return new PageRequest(pageNumber, pageSize);
	}
	
	public Pageable getPage(Sort sort) {
		return new PageRequest(pageNumber, pageSize, sort);
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
}
