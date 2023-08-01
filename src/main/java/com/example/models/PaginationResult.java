package com.example.models;

import java.util.List;

public class PaginationResult<Employee> {
    public int pageNumber;
    public int pageSize;
    public int totalPages;
    public long totalCount;
    public List<Employee> data;

    public PaginationResult(int pageNumber, int pageSize, int totalPages, long totalCount, List<Employee> data) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.data = data;
    }


}
