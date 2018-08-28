package org.alliancegenome.core.service;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;
import org.alliancegenome.neo4j.view.View;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Setter
@Getter
public class JsonResultResponse<T> {

    @JsonView(View.OrthologyView.class)
    private List<T> results;
    @JsonView(View.OrthologyView.class)
    private int total;
    @JsonView(View.OrthologyView.class)
    private int returnedRecords;
    @JsonView(View.OrthologyView.class)
    private String errorMessage = "";
    @JsonView(View.OrthologyView.class)
    private String requestDuration;
    @JsonView(View.OrthologyView.class)
    private Request request;
    @JsonView(View.OrthologyView.class)
    private String apiVersion;

    public void setRequestDuration(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = new Duration(startTime, endTime);
        requestDuration = duration.toString();
    }

    public void setResults(List<T> results) {
        this.results = results;
        if (results != null)
            returnedRecords = results.size();
    }

    public void setRequest(HttpServletRequest request) {
        this.request = new Request();
        this.request.setUri(request.getRequestURI());
        this.request.setParameterMap(request.getParameterMap());
    }

    @Setter
    @Getter
    class Request {
        @JsonView(View.OrthologyView.class)
        String uri;
        @JsonView(View.OrthologyView.class)
        TreeMap<String, String[]> parameterMap;

        void setParameterMap(Map<String, String[]> parameterMap) {
            this.parameterMap = new TreeMap<>(parameterMap);
        }
    }
}
