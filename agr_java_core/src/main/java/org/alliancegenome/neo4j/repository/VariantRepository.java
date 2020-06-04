package org.alliancegenome.neo4j.repository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.alliancegenome.neo4j.entity.node.Variant;

public class VariantRepository extends Neo4jRepository<Variant> {

    public VariantRepository() {
        super(Variant.class);
    }

    public List<Variant> getVariantsOfAllele(String id) {
        HashMap<String, String> map = new HashMap<>();
        String paramName = "alleleID";
        map.put(paramName, id);
        String query = "";
        query += " MATCH p1=(a:Allele)<-[:VARIATION]-(variant:Variant)--(soTerm:SOTerm) ";
        query += " WHERE a.primaryKey = {" + paramName + "}";
        query += " OPTIONAL MATCH consequence=(:GeneLevelConsequence)<-[:ASSOCIATION]-(variant:Variant)";
        query += " OPTIONAL MATCH loc=(variant:Variant)-[:ASSOCIATION]->(:GenomicLocation)-[:ASSOCIATION]->(:Chromosome)";
        query += " OPTIONAL MATCH p2=(variant:Variant)<-[:COMPUTED_GENE]-(:Gene)-[:ASSOCIATION]->(:GenomicLocation)-[:ASSOCIATION]->(:Chromosome)";
        query += " RETURN p1, p2, loc, consequence ";

        Iterable<Variant> alleles = query(query, map);
        return StreamSupport.stream(alleles.spliterator(), false)
                .collect(Collectors.toList());


    }

    public Variant getVariant(String variantID) {
        HashMap<String, String> map = new HashMap<>();
        String paramName = "variantID";
        map.put(paramName, variantID);
        String query = "";
        query += " MATCH p1=(t:Transcript)-[:ASSOCIATION]->(variant:Variant)--(soTerm:SOTerm) ";
        query += " WHERE variant.primaryKey = {" + paramName + "}";
        query += " OPTIONAL MATCH consequence=(variant:Variant)-[:ASSOCIATION]->(:TranscriptLevelConsequence)<-[:ASSOCIATION]-(t:Transcript)<-[:TRANSCRIPT_TYPE]-(:SOTerm)";
        query += " OPTIONAL MATCH gene=(t:Transcript)-[:TRANSCRIPT]-(:Gene)";
        query += " RETURN p1, consequence, gene ";

        Iterable<Variant> variants = query(query, map);
        for (Variant a : variants) {
            if (a.getPrimaryKey().equals(variantID)) {
                return a;
            }
        }
        return null;
    }
}