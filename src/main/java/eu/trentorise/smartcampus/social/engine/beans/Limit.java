/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
