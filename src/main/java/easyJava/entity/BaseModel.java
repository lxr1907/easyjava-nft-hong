package easyJava.entity;

import java.io.Serializable;

/**
 * 分页基础
 */
public class BaseModel implements Serializable {

	private Integer pageSize = 10;// 每页大小
	private Integer pageNo = 1;// 当前页数
	private Long fromRec;// 当前查询的索引位置
	private String orderColumn="id";// 排序字段
	private String orderAsc="asc";// 排序规则

	public String getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}

	public String getOrderAsc() {
		return orderAsc;
	}

	public void setOrderAsc(String orderAsc) {
		this.orderAsc = orderAsc;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public BaseModel setPageSize(Integer pageSize) {
		if (pageSize == null || pageSize < 1) {
			pageSize = 10;
		}

		this.pageSize = pageSize;
		return this;
	}

	public Integer getPageNo() {
		return this.pageNo;
	}

	public BaseModel setPageNo(Integer pageNo) {
		if (pageNo == null || pageNo < 1) {
			pageNo = 1;
		}

		this.pageNo = pageNo;
		return this;
	}

	public Long getFromRec() {
		if (this.pageNo != null && this.pageSize != null) {
			this.fromRec = Long.valueOf(String.valueOf((this.pageNo - 1) * this.pageSize));
		}
		return this.fromRec;
	}

	public BaseModel setFromRec(Long fromRec) {
		this.fromRec = fromRec;
		return this;
	}

	public void setPageFieldToNull() {
		this.pageSize = null;
		this.pageNo = null;
		this.fromRec = null;
	}
}
