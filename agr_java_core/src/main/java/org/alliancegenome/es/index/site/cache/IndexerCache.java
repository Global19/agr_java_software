package org.alliancegenome.es.index.site.cache;

import java.util.*;

import org.alliancegenome.es.index.site.document.SearchableItemDocument;
import org.alliancegenome.neo4j.entity.node.Allele;
import org.alliancegenome.neo4j.entity.node.Variant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexerCache {

    protected Map<String, Variant> variantMap = new HashMap<>();
    protected Map<String, Allele> alleleMap = new HashMap<>();

    protected Map<String, Set<String>> chromosomes = new HashMap<>();
    protected Map<String, Set<String>> constructs = new HashMap<>();
    protected Map<String, Set<String>> crossReferences = new HashMap<>();
    protected Map<String, Set<String>> diseases = new HashMap<>();
    protected Map<String, Set<String>> diseasesAgrSlim = new HashMap<>();
    protected Map<String, Set<String>> diseasesWithParents = new HashMap<>();
    protected Map<String, Set<String>> dnaChangeTypesMap = new HashMap<>();
    protected Map<String, Set<String>> expressionStages = new HashMap<>();
    protected Map<String, Set<String>> alleles = new HashMap<>();
    protected Map<String, Set<String>> genes = new HashMap<>();
    protected Map<String, Set<String>> models = new HashMap<>();
    protected Map<String, Set<String>> molecularConsequenceMap = new HashMap<>();
    protected Map<String, Set<String>> phenotypeStatements = new HashMap<>();
    protected Map<String, Double> popularity = new HashMap<>();
    protected Map<String, Set<String>> relatedVariants = new HashMap<>();
    protected Map<String, Set<String>> relatedVariantSynonyms = new HashMap<>();
    protected Map<String, Set<String>> secondaryIds = new HashMap<>();
    protected Map<String, Set<String>> species = new HashMap<>();
    protected Map<String, Set<String>> synonyms = new HashMap<>();

    public void addCachedFields(Iterable<SearchableItemDocument> documents) {
        for (SearchableItemDocument document : documents) {
            addCachedFields(document);
        }
    }

    public void addCachedFields(SearchableItemDocument document) {
        String id = document.getPrimaryKey();

        document.setAlleles(alleles.get(id));
        //addAll vs setter is because some fields may be set by a translator before this step
        if (crossReferences.get(id) != null) {
            document.getCrossReferences().addAll(crossReferences.get(id));
        }
        if (chromosomes.get(id) != null) {
            document.getChromosomes().addAll(chromosomes.get(id));
        }
        document.setConstructs(constructs.get(id));
        document.setDiseases(diseases.get(id));
        document.setDiseasesAgrSlim(diseasesAgrSlim.get(id));
        document.setDiseasesWithParents(diseasesWithParents.get(id));

        if (dnaChangeTypesMap.get(id) == null) {
            Set<String> defaultValue = new HashSet<>();
            defaultValue.add("unreported");
            document.setDnaChangeTypes(defaultValue);
        } else {
            document.setDnaChangeTypes(dnaChangeTypesMap.get(id));
        }

        document.setExpressionStages(expressionStages.get(id));
        document.setGenes(genes.get(id));
        document.setModels(models.get(id));

        if (molecularConsequenceMap.get(id) != null) {
            document.setMolecularConsequence(new HashSet<>());
            for (String consequence : molecularConsequenceMap.get(id)) {
                document.getMolecularConsequence().addAll(Arrays.asList(consequence.split(",")));
            }
        }

        document.setPhenotypeStatements(phenotypeStatements.get(id));
        document.setPopularity(popularity.get(id) == null ? 0D : popularity.get(id));
        document.setRelatedVariants(relatedVariants.get(id));
        document.setRelatedVariantSynonyms(relatedVariantSynonyms.get(id));
        if (secondaryIds.get(id) != null) {
            document.getSecondaryIds().addAll(secondaryIds.get(id));
        }

        //awkwardly collapsing to a single value, multi-valued species should
        //be captured in the associatedSpecies field
        if (species.get(id) != null) {
            Set<String> speciesSet = species.get(id);
            String speciesName = speciesSet.stream().findFirst().get();
            if (speciesName != null) {
                document.setSpecies(speciesName);
            }
        }

        if (synonyms.get(id) != null) {
            document.getSynonyms().addAll(synonyms.get(id));
        }

    }

}
