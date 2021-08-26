package com.photo.editor.picskills.photoeditorpro.model;

import java.util.List;

public class SimpleFilterModel {
    private String filterCategory;
    private List<TotalNumberOfFilter> totalNumberOfFilter;

    public String getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(String filterCategory) {
        this.filterCategory = filterCategory;
    }

    public List<TotalNumberOfFilter> getTotalNumberOfFilter() {
        return totalNumberOfFilter;
    }

    public void setTotalNumberOfFilter(List<TotalNumberOfFilter> totalNumberOfFilter) {
        this.totalNumberOfFilter = totalNumberOfFilter;
    }
}
