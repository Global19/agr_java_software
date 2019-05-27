package org.alliancegenome.neo4j.repository;

import org.alliancegenome.core.ExpressionDetail;
import org.alliancegenome.core.service.*;
import org.alliancegenome.es.model.query.Pagination;
import org.alliancegenome.neo4j.entity.node.BioEntityGeneExpressionJoin;
import org.alliancegenome.neo4j.entity.node.GOTerm;
import org.alliancegenome.neo4j.entity.node.UBERONTerm;
import org.alliancegenome.neo4j.view.BaseFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class ExpressionCacheRepository {

    public PaginationResult<ExpressionDetail> getExpressionAnnotations(List<String> geneIDs, String termID, Pagination pagination) {
        checkCache();
        if (caching)
            return null;

        List<ExpressionDetail> fullExpressionAnnotationList = new ArrayList<>();
        geneIDs.forEach(geneID -> fullExpressionAnnotationList.addAll(geneExpressionMap.get(geneID)));

        //filtering
        // filter on termID
        List<ExpressionDetail> filterTermIDList = fullExpressionAnnotationList;
        if (termID != null) {
            filterTermIDList = fullExpressionAnnotationList.stream()
                    .filter(expressionDetail -> expressionDetail.getTermIDs().contains(termID))
                    .collect(Collectors.toList());
        }
        List<ExpressionDetail> filteredExpressionAnnotationList = filterExpressionAnnotations(filterTermIDList, pagination.getFieldFilterValueMap());

        PaginationResult<ExpressionDetail> result = new PaginationResult<>();
        if (!filteredExpressionAnnotationList.isEmpty()) {
            result.setTotalNumber(filteredExpressionAnnotationList.size());
            result.setResult(getSortedAndPaginatedExpressions(filteredExpressionAnnotationList, pagination));
        }
        return result;
    }

    private List<ExpressionDetail> filterExpressionAnnotations(List<ExpressionDetail> expressionDetails, BaseFilter fieldFilterValueMap) {
        if (expressionDetails == null)
            return null;
        if (fieldFilterValueMap == null)
            return expressionDetails;
        return expressionDetails.stream()
                .filter(annotation -> containsFilterValue(annotation, fieldFilterValueMap))
                .collect(Collectors.toList());
    }

    private boolean containsFilterValue(ExpressionDetail annotation, BaseFilter fieldFilterValueMap) {
        // remove entries with null values.
        fieldFilterValueMap.values().removeIf(Objects::isNull);
        Set<Boolean> filterResults = fieldFilterValueMap.entrySet().stream()
                .map((entry) -> {
                    FilterFunction<ExpressionDetail, String> filterFunction = ExpressionAnnotationFiltering.filterFieldMap.get(entry.getKey());
                    if (filterFunction == null)
                        return null;
                    return filterFunction.containsFilterValue(annotation, entry.getValue());
                })
                .collect(Collectors.toSet());

        return !filterResults.contains(false);
    }

    private List<ExpressionDetail> getSortedAndPaginatedExpressions(List<ExpressionDetail> expressionList, Pagination pagination) {
        // sorting
        SortingField sortingField = null;
        String sortBy = pagination.getSortBy();
        if (sortBy != null && !sortBy.isEmpty())
            sortingField = SortingField.getSortingField(sortBy.toUpperCase());

        ExpressionAnnotationSorting sorting = new ExpressionAnnotationSorting();
        expressionList.sort(sorting.getComparator(sortingField, pagination.getAsc()));

        // paginating
        return expressionList.stream()
                .skip(pagination.getStart())
                .limit(pagination.getLimit())
                .collect(Collectors.toList());
    }

    private Log log = LogFactory.getLog(getClass());
    // cached value
    private static List<ExpressionDetail> allExpression = null;
    // Map<gene ID, List<Allele>> grouped by gene ID
    private static Map<String, List<ExpressionDetail>> geneExpressionMap;

    private static boolean caching;

    private void checkCache() {
        if (allExpression == null && !caching) {
            caching = true;
            cacheAllExpression();
            caching = false;
        }
        if (caching)
            throw new RuntimeException("Expression records are still being cached. Please wait...");
    }

    private void cacheAllExpression() {
        long startTime = System.currentTimeMillis();
        GeneRepository geneRepository = new GeneRepository();
        ExpressionCacheRepository expressionRepository = new ExpressionCacheRepository();
        List<BioEntityGeneExpressionJoin> joins = geneRepository.getAllExpressionAnnotations();

        allExpression = joins.stream()
                .map(expressionJoin -> {
                    ExpressionDetail detail = new ExpressionDetail();
                    detail.setGene(expressionJoin.getGene());
                    detail.setTermName(expressionJoin.getEntity().getWhereExpressedStatement());
                    detail.setAssay(expressionJoin.getAssay());
                    detail.setDataProvider(expressionJoin.getGene().getDataProvider());
                    if (expressionJoin.getStage() != null)
                        detail.setStage(expressionJoin.getStage());
                    detail.setPublications(new TreeSet<>(expressionJoin.getPublications()));
                    detail.setCrossReference(expressionJoin.getCrossReference());
                    List<String> aoList = expressionJoin.getEntity().getAoTermList().stream().map(UBERONTerm::getPrimaryKey).collect(Collectors.toList());
                    Set<String> parentTermIDs = expressionRepository.getParentTermIDs(aoList);
                    aoList.addAll(parentTermIDs);
                    detail.addTermIDs(aoList);
                    detail.addTermIDs(expressionJoin.getEntity().getCcRibbonTermList().stream().map(GOTerm::getPrimaryKey).collect(Collectors.toList()));
                    if (expressionJoin.getStageTerm() != null)
                        detail.addTermID(expressionJoin.getStageTerm().getPrimaryKey());
                    return detail;
                })
                .collect(Collectors.toList());

        geneExpressionMap = allExpression.stream()
                .collect(groupingBy(expressionDetail -> expressionDetail.getGene().getPrimaryKey()));

        log.info("Number of all expression records: " + allExpression.size());
        log.info("Number of all Genes with Expression: " + geneExpressionMap.size());
        log.info("Time to create cache: " + (System.currentTimeMillis() - startTime) / 1000);

    }

    private static List<String> parentTermIDs = new ArrayList<>();

    static {
        // anatomical entity
        parentTermIDs.add("UBERON:0001062");
        // life cycle stage
        parentTermIDs.add("UBERON:0000105");
        // cellular Component
        parentTermIDs.add("GO:0005575");
    }

    private Set<String> getParentTermIDs(List<String> aoList) {
        if (aoList == null || aoList.isEmpty())
            return null;
        DiseaseRepository repository = new DiseaseRepository();
        Set<String> parentSet = new HashSet<>(4);
        Map<String, Set<String>> map = repository.getClosureMappingUberon();
        aoList.forEach(id -> {
            parentTermIDs.forEach(parentTermID -> {
                if (map.get(parentTermID) != null && map.get(parentTermID).contains(id))
                    parentSet.add(parentTermID);
            });
            if (id.equals("UBERON:AnatomyOtherLocation"))
                parentSet.add(parentTermIDs.get(0));
        });
        return parentSet;
    }

}
