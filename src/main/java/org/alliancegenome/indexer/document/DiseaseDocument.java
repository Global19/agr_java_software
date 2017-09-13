package org.alliancegenome.indexer.document;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiseaseDocument extends ESDocument {

    private String doId;
    private String primaryKey;
    private String primaryId;
    private String category = "disease";
    private String name;
    @JsonProperty("name_key")
    private String nameKey;
    private String definition;
    private List<AnnotationDocument> annotations;
    private List<DiseaseDocument> parents;
    private List<DiseaseDocument> children;
    private List<String> synonyms;
    private List<String> external_ids;
    private List<SourceDoclet> sourceList;

    @JsonIgnore
    public String getDocumentId() {
        return primaryKey;
    }
}
