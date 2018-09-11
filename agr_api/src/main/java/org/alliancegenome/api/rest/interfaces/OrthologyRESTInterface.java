package org.alliancegenome.api.rest.interfaces;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/homologs")
@Api(value = "Homology")
@Consumes(MediaType.APPLICATION_JSON)
public interface OrthologyRESTInterface {

    @GET
    @Path("/{taxonIDOne}/{taxonIDTwo}")
    @ApiOperation(value = "Retrieve homologous gene records for given pair of species")
    String getDoubleSpeciesOrthology(
            @ApiParam(name = "taxonIDOne", value = "Taxon ID for the first gene: Could be the full ID, e.g. 'NCBITaxon:10090', or just the ID, i.e. '10090'. Alternatively, part of a species name uniquely identifying a single species, e.g. 'danio' or 'mus'.", required = true, type = "String")
            @PathParam("taxonIDOne") String speciesOne,
            @ApiParam(name = "taxonIDTwo", value = "Taxon ID for the second gene: Could be the full ID, e.g. 'NCBITaxon:10090', or just the ID, i.e. '10090'. Alternatively, part of a species name uniquely identifying a single species, e.g. 'danio' or 'mus'.", required = true, type = "String")
            @PathParam("taxonIDTwo") String speciesTwo,
            @ApiParam(value = "Select a stringency filter", allowableValues = "stringent, moderate, all", defaultValue = "stringent")
            @QueryParam("stringencyFilter") String stringencyFilter,
            @ApiParam(value = "Select a calculation method", allowableValues = "Ensembl Compara, HGNC, Hieranoid, InParanoid, OMA, OrthoFinder, OrthoInspector, PANTHER, PhylomeDB, Roundup, TreeFam, ZFIN")
            @QueryParam("methods") String methods,
            @ApiParam(value = "Number of returned rows")
            @DefaultValue("20") @QueryParam("rows") Integer rows,
            @ApiParam(value = "Starting row")
            @DefaultValue("1") @QueryParam("start") Integer start) throws IOException;

    @GET
    @Path("/{taxonID}")
    @ApiOperation(value = "Retrieve homologous gene records for a given species")
    String getSingleSpeciesOrthology(
            @ApiParam(name = "taxonID", value = "Taxon ID for the gene: Could be the full ID, e.g. 'NCBITaxon:10090', or just the ID, i.e. '10090'. Alternatively, part of a species name uniquely identifying a single species, e.g. 'danio' or 'mus'.", required = true, type = "String")
            @PathParam("taxonID") String species,
            @ApiParam(value = "Select a stringency filter", allowableValues = "stringent, moderate, all", defaultValue = "stringent")
            @QueryParam("stringencyFilter") String stringencyFilter,
            @ApiParam(value = "Select a calculation method", allowableValues = "Ensembl Compara, HGNC, Hieranoid, InParanoid, OMA, OrthoFinder, OrthoInspector, PANTHER, PhylomeDB, Roundup, TreeFam, ZFIN")
            @QueryParam("methods") String methods,
            @ApiParam(value = "Number of returned rows")
            @DefaultValue("20") @QueryParam("rows") Integer rows,
            @ApiParam(value = "Starting row")
            @DefaultValue("1") @QueryParam("start") Integer start) throws IOException;

    @GET
    @Path("/species")
    @ApiOperation(value = "Retrieve homologous gene records for given list of species")
    String getMultiSpeciesOrthology(
            @ApiParam(name = "taxonID", value = "List of taxon IDs for which homology is retrieved, e.g. 'NCBITaxon:10090'")
            @QueryParam("taxonID") List<String> taxonID,
            @ApiParam(name = "taxonIdList", value = "List of source taxon IDs for which homology is retrieved in a comma-delimited list, e.g. 'MGI:109583,RGD:2129,MGI:97570")
            @QueryParam("taxonIdList") String taxonIdList,
            @ApiParam(value = "Select a stringency filter", allowableValues = "stringent, moderate, all", defaultValue = "stringent")
            @QueryParam("stringencyFilter") String stringencyFilter,
            @ApiParam(value = "Select a calculation method", allowableValues = "Ensembl Compara, HGNC, Hieranoid, InParanoid, OMA, OrthoFinder, OrthoInspector, PANTHER, PhylomeDB, Roundup, TreeFam, ZFIN")
            @QueryParam("methods") String methods,
            @ApiParam(value = "Number of returned rows")
            @DefaultValue("20") @QueryParam("rows") Integer rows,
            @ApiParam(value = "Starting row")
            @DefaultValue("1") @QueryParam("start") Integer start) throws IOException;

    @GET
    @Path("/genes")
    @ApiOperation(value = "Retrieve homologous gene records for given list of genes")
    String getMultiGeneOrthology(
            @ApiParam(name = "geneID", value = "List of genes (specified by their ID) for which homology is retrieved, e.g. 'MGI:109583'")
            @QueryParam("geneID") List<String> geneID,
            @ApiParam(name = "geneIdList", value = "List of additional source gene IDs for which homology is retrieved in a comma-delimited list, e.g. 'MGI:109583,RGD:2129,MGI:97570")
            @QueryParam("geneIdList") String geneList,
            @ApiParam(value = "Select a stringency filter", allowableValues = "stringent, moderate, all", defaultValue = "stringent")
            @QueryParam("stringencyFilter") String stringencyFilter,
            @ApiParam(value = "calculation methods", allowableValues = "Ensembl Compara, HGNC, Hieranoid, InParanoid, OMA, OrthoFinder, OrthoInspector, PANTHER, PhylomeDB, Roundup, TreeFam, ZFIN")
            @QueryParam("methods") List<String> methods,
            @ApiParam(value = "Number of returned rows")
            @DefaultValue("20") @QueryParam("rows") Integer rows,
            @ApiParam(value = "Starting row")
            @DefaultValue("1") @QueryParam("start") Integer start) throws IOException;

    @GET
    @Path("/methods")
    @ApiOperation(value = "Retrieve all methods used for calculation of homology")
    String getAllMethodsCalculations() throws JsonProcessingException;
}