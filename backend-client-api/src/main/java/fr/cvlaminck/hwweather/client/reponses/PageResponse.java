package fr.cvlaminck.hwweather.client.reponses;

import java.util.Collection;
import java.util.Collections;

public class PageResponse<T> {
    private String query;
    private Collection<T> results = Collections.emptyList();

    private int totalNumberOfResults;

    private int page;
    private int numberOfResultsPerPage;

    public int getNumberOfPage() {
        return (int) Math.ceil(((double) totalNumberOfResults) / numberOfResultsPerPage);
    }

    public boolean hasNext() {
        return page < getNumberOfPage();
    }

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

    public int getTotalNumberOfResults() {
        return totalNumberOfResults;
    }

    public void setTotalNumberOfResults(int totalNumberOfResults) {
        this.totalNumberOfResults = totalNumberOfResults;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNumberOfResultsPerPage() {
        return numberOfResultsPerPage;
    }

    public void setNumberOfResultsPerPage(int numberOfResultsPerPage) {
        this.numberOfResultsPerPage = numberOfResultsPerPage;
    }
}
