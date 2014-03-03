package eu.trentorise.smartcampus.social.engine.beans;

import java.util.List;

public class Limit {
	private int page;
	private int pageSize;
	
	private List<String> sortList;
	private int direction = 0;		//0 -> asc, 1 -> desc;

	private long fromDate;
	private long toDate;

	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}	

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getSortList() {
		return sortList;
	}

	public int getDirection() {
		return direction;
	}

	public void setSortList(List<String> sortList) {
		this.sortList = sortList;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public long getFromDate() {
		return fromDate;
	}

	public void setFromDate(long fromDate) {
		this.fromDate = fromDate;
	}

	public long getToDate() {
		return toDate;
	}

	public void setToDate(long toDate) {
		this.toDate = toDate;
	}

}
