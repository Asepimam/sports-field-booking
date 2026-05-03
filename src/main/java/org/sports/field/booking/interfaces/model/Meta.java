package org.sports.field.booking.interfaces.model;

public class Meta {

    private int page;
    private int size;
    private long total;
    private int totalPages;

    public Meta(int page, int size, long total) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
