package org.alliancegenome.indexer.translators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.alliancegenome.indexer.document.SearchableItemDocument;
import org.alliancegenome.indexer.entity.ExternalId;
import org.alliancegenome.indexer.entity.GOTerm;
import org.alliancegenome.indexer.entity.Gene;
import org.alliancegenome.indexer.entity.SecondaryId;
import org.alliancegenome.indexer.entity.Synonym;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeneToSearchableItemTranslator extends EntityDocumentTranslator<Gene, SearchableItemDocument> {

	private Logger log = LogManager.getLogger(getClass());

	@Override
	protected SearchableItemDocument entityToDocument(Gene entity) {
		log.info(entity);
		HashMap<String, ArrayList<String>> goTerms = new HashMap<String, ArrayList<String>>();

		SearchableItemDocument s = new SearchableItemDocument();

		s.setCategory("gene");
		// TODO s.setCrossReferences(crossReferences);
		s.setDataProvider(entity.getDataProvider());
		s.setDescription(entity.getDescription());
		// TODO s.setDiseases(diseases);

		ArrayList<String> external_ids = new ArrayList<String>();
		if(entity.getExternalIds() != null) {
			for(ExternalId externalId: entity.getExternalIds()) {
				external_ids.add(externalId.getName());
			}
		}
		s.setExternal_ids(external_ids);

		// Setup Go Terms by type
		for(GOTerm term: entity.getGOTerms()) {
			ArrayList<String> list = goTerms.get(term.getType());
			if(list == null) {
				list = new ArrayList<String>();
				goTerms.put(term.getType(), list);
			}
			if(!list.contains(term.getName())) {
				list.add(term.getName());
			}
		}

		s.setGene_biological_process(goTerms.get("biological_process"));
		s.setGene_cellular_component(goTerms.get("cellular_component"));
		s.setGene_molecular_function(goTerms.get("molecular_function"));

		s.setGeneLiteratureUrl(entity.getGeneLiteratureUrl());
		s.setGeneSynopsis(entity.getGeneSynopsis());
		s.setGeneSynopsisUrl(entity.getGeneSynopsisUrl());

		// TODO s.setGenomeLocations(genomeLocations);
		s.setHref(null); // This might look wrong but it was taken from the old AGR code base.
		// TODO s.setModCrossReference(modCrossReference);
		s.setName(entity.getName());
		s.setName_key(entity.getSymbol()); // This might look wrong but it was taken from the old AGR code base.
		// TODO s.setOrthology(orthology);
		s.setPrimaryId(entity.getPrimaryKey());
		s.setRelease(entity.getCreatedBy().getRelease());

		ArrayList<String> secondaryIds = new ArrayList<String>();
		if(entity.getSecondaryIds() != null) {
			for(SecondaryId secondaryId: entity.getSecondaryIds()) {
				external_ids.add(secondaryId.getName());
			}		
		}
		s.setSecondaryIds(secondaryIds);

		if(entity.getSOTerm() != null) {
			s.setSoTermId(entity.getSOTerm().getPrimaryKey());
			s.setSoTermName(entity.getSOTerm().getName());
		}
		s.setSymbol(entity.getSymbol());

		ArrayList<String> synonyms = new ArrayList<String>();
		if(entity.getSynonyms() != null) {
			for(Synonym synonym: entity.getSynonyms()) {
				synonyms.add(synonym.getName());
			}
		}
		s.setSynonyms(synonyms);

		s.setTaxonId(entity.getTaxonId());
		log.info(s);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			log.info("JSON: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(s));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return s;
	}

	@Override
	protected Gene doumentToEntity(SearchableItemDocument doument) {
		// We are not going to the database yet so will implement this when we need to
		return null;
	}

}
