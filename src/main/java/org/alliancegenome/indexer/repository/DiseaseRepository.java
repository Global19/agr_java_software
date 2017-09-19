package org.alliancegenome.indexer.repository;

import org.alliancegenome.indexer.entity.node.DOTerm;
import org.alliancegenome.indexer.entity.node.Gene;
import org.neo4j.ogm.exception.MappingException;
import org.neo4j.ogm.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DiseaseRepository extends Neo4jRepository<DOTerm> {

	public DiseaseRepository() {
		super(DOTerm.class);
	}

	public List<DOTerm> getAllDiseaseTerms(int start, int maxSize) {
		String cypher = "match (root:DOTerm) WHERE  root.is_obsolete = 'false' " +
				"WITH root SKIP " + start + " LIMIT " + maxSize + " " +
				"optional match (diseaseGeneJoin:DiseaseGeneJoin)-[q:ASSOCIATION]->(root), " +
				"(gene:Gene)-[geneDiseaseRelation:ASSOCIATION]->(diseaseGeneJoin), " +
				"(publication:Publication)<-[publicationRelation]-(diseaseGeneJoin), " +
				"(evidence:EvidenceCode)<-[evidenceRelation:EVIDENCE]-(diseaseGeneJoin), " +
				"(species:Species)<-[speciesRelation:FROM_SPECIES]-(gene), " +
				"(root)-[crossReferenceRelation:CROSS_REFERENCE]->(crossReference:CrossReference) " +
				"return distinct root, q,diseaseGeneJoin, geneDiseaseRelation, gene, publicationRelation, publication, evidenceRelation, evidence, " +
				"species, speciesRelation, crossReference, crossReferenceRelation ";
		return (List<DOTerm>) query(cypher);
	}

	public List<DOTerm> getAllDiseaseTerms() {
		String cypher = "match (root:DOTerm) " +
				"optional match (a:Association)-[q:ASSOCIATION]->(root), " +
				"(m:Gene)-[qq:ASSOCIATION]->(a), " +
				"(p:Publication)<-[qqq*]-(a), " +
				"(e:EvidenceCode)<-[ee:EVIDENCE]-(a), " +
				"(s:Species)<-[ss:FROM_SPECIES]-(m), " +
				"(root)-[ex:CROSS_REFERENCE]->(exx:CrossReference), " +
				"(root)-[synonymRelation:ALSO_KNOWN_AS]->(synonym:Synonym)  " +
				"optional match (parent:DOTerm)<-[parentRelation:IS_A]-(root:DOTerm), " +
				"(child:DOTerm)-[childRelation:IS_A]->(root:DOTerm) " +
				"return distinct root, q,a,qq,m,qqq,p, ee, e, s, ss, ex, exx, parent, " +
				"parentRelation, child, childRelation, synonymRelation, synonym ";
		return (List<DOTerm>) query(cypher);
	}

	public List<DOTerm> getDiseaseTermsWithAnnotations() {
		String cypher = "match (root:DOTerm),  " +
				"(a:Association)-[q:ASSOCIATION]->(root), " +
				"(gene:Gene)-[geneAssociation:ASSOCIATION]->(a), " +
				"(publication:Publication)<-[qqq]-(a), " +
				"(evidence:EvidenceCode)<-[evidenceRelation:EVIDENCE]-(a), " +
				"(species:Species)<-[speciesRelation:FROM_SPECIES]-(gene), " +
				"(root)-[parentChildRelation:IS_A*]->(parent:DOTerm) " +
				"return root, q,a, geneAssociation,gene ,qqq, publication, evidenceRelation, evidence, " +
				"species, speciesRelation, parentChildRelation, parent";

		return (List<DOTerm>) query(cypher);
	}

	public List<DOTerm> getAllTerms() {
		String cypher = "match (root:DOTerm) " +
				"optional match (parent:DOTerm)<-[parentRelation:IS_A]-(root:DOTerm), " +
				"(child:DOTerm)-[childRelation:IS_A]->(root:DOTerm), " +
				"(synonym:Synonym)<-[synonymRelation:ALSO_KNOWN_AS]-(root:DOTerm) " +
				"return root, parent, " +
				"parentRelation, child, childRelation, synonym, synonymRelation";
		return (List<DOTerm>) query(cypher);
	}

	public long getDiseaseTermsWithAnnotationsCount() {
		String cypher = "match (root:DOTerm), " +
				"(a:Association)-[q:ASSOCIATION]->(root), " +
				"(m:Gene)-[qq:ASSOCIATION]->(a), " +
				"(p:Publication)<-[qqq*]-(a), " +
				"(e:EvidenceCode)<-[ee:EVIDENCE]-(a), " +
				"(s:Species)<-[ss:FROM_SPECIES]-(m), " +
				"(root)-[ex:CROSS_REFERENCE]->(exx:CrossReference) " +
				"return count(root)";
		Long s = queryCount(cypher);
		return s;
	}

	public List<String> getAllDiseaseKeys() {
		String query = "MATCH (d:DOTerm) RETURN d.primaryKey";
		
		Result r = queryForResult(query);
		Iterator<Map<String, Object>> i = r.iterator();
		
		ArrayList<String> list = new ArrayList<>();
		
		while(i.hasNext()) {
			Map<String, Object> map2 = i.next();
			list.add((String)map2.get("d.primaryKey"));
		}
		return list;
	}
	
	public DOTerm getDiseaseTerm(String primaryKey) {

		String cypher = "MATCH p0=(d:DOTerm)--(s) WHERE d.primaryKey = {primaryKey} " +
				" OPTIONAL MATCH p1=(d)--(s:DiseaseGeneJoin)-[:EVIDENCE]-(eq), p2=(s)--(g:Gene)" +
				" OPTIONAL MATCH p3=(d)-[:IS_A]-(d2)" + 
				" RETURN p0, p1, p2, p3";

		HashMap<String, String> map = new HashMap<>();
		map.put("primaryKey", primaryKey);

		try {
			Iterable<DOTerm> terms = query(cypher, map);
			for(DOTerm d: terms) {
				if(d.getPrimaryKey().equals(primaryKey)) {
					return d;
				}
			}
		} catch (MappingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
