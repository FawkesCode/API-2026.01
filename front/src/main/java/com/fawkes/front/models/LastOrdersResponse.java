package com.fawkes.front.models;

import java.util.List;

public class LastOrdersResponse<T> {
    private List<T> content;
    private int number;
    private int totalPages;
    private long totalElements;
    private int size;

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getNumber() { return number; }
    public int getTotalPages() { return totalPages; }
    public long getTotalElements() { return totalElements; }

}
