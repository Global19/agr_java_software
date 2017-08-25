package org.alliancegenome.indexer.entity;

import java.util.List;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import lombok.Getter;
import lombok.Setter;

@NodeEntity
@Getter
@Setter
public class DOTerm extends Neo4jNode {

    private String doUrl;
    private String doDisplayId;
    private String doId;
    private String doPrefix;
    private String primaryKey;
    private String name;
    private String definition;

    @Relationship(type = "IS_IMPLICATED_IN", direction = Relationship.INCOMING)
    private List<Gene> genes;

    @Relationship(type = "ASSOCIATION", direction = Relationship.INCOMING)
    private List<Association> associations;

    @Relationship(type = "IS_A", direction = Relationship.INCOMING)
    private List<DOTerm> children;

    @Relationship(type = "IS_A")
    private List<DOTerm> parents;

    @Relationship(type = "ALSO_KNOWN_AS")
    private List<Synonym> synonyms;

    @Relationship(type = "ALSO_KNOWN_AS")
    private List<ExternalId> externalIds;

}
