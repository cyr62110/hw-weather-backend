package fr.cvlaminck.hwweather.client.reponses;

import java.util.Collection;
import java.util.Collections;

public class PageResponse<T> {
    private String query;
    private Collection<T> results = Collections.emptyList();

    private int totalNumberOfResult;

    private int page;
    private int numberOfResultPerPage;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Collection<T> getResults() {
        return results;
    }

    public void setResults(Collection<T> results) {
        this.results = results;
    }

    public int getTotalNumberOfResult() {
        return totalNumberOfResult;
    }

    public void setTotalNumberOfResult(int totalNumberOfResult) {
        this.totalNumberOfResult = totalNumberOfResult;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNumberOfResultPerPage() {
        return numberOfResultPerPage;
    }

    public void setNumberOfResultPerPage(int numberOfResultPerPage) {
        this.numberOfResultPerPage = numberOfResultPerPage;
    }
}
