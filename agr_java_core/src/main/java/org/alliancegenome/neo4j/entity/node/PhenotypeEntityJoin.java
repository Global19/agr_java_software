package org.alliancegenome.neo4j.entity.node;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity
@Getter
@Setter
public class PhenotypeEntityJoin extends EntityJoin {

    private String primaryKey;
    private String joinType;

    @Relationship(type = "EVIDENCE")
    private Publication publication;

    @Relationship(type = "ASSOCIATION", direction = Relationship.INCOMING)
    private Gene gene;

    @Relationship(type = "ASSOCIATION", direction = Relationship.INCOMING)
    private Feature feature;

    @Relationship(type = "ASSOCIATION")
    private Phenotype phenotype;

}