package org.alliancegenome.variant_indexer.es.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantDocument {
    private String id;
    private String chromosome;
    private int startPos;
    private int endPos;
    private String refNuc;
    private String varNuc;
    private String qual;
    private String variantType;
    private String filter;
    private String refPep;
    private String aa; // ancestral allele
    private String MA; //minor allele
    private double MAF;    // minor allele frequency
    private int MAC;    // minor allele count
    private List<String> evidence;
    private List<String> clinicalSignificance;
    private List<TranscriptFeature> consequences;
}