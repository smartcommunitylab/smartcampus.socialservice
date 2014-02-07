package eu.trentorise.smartcampus.social.engine.beans;

public class Limit {
	private int page;
	private int pageSize;

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
