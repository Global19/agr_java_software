package org.alliancegenome.es.index.dao;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.ExecutionException;

import org.alliancegenome.core.config.ConfigHelper;
import org.alliancegenome.es.index.document.ESDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ESDocumentDAO<D extends ESDocument> extends ESDAO {

	private Log log = LogFactory.getLog(getClass());
	private ObjectMapper mapper = new ObjectMapper();

	public void createDocumnet(D doc) {
		log.debug("Creating new ES doc: " + doc);
		try {
			String json = mapper.writeValueAsString(doc);
			log.info("Creating Document JSON: " + json);
			IndexRequest indexRequest = new IndexRequest();
			indexRequest.index(ConfigHelper.getEsDataIndex());
			indexRequest.id(doc.getDocumentId());
			indexRequest.type(doc.getType());
			indexRequest.source(json, XContentType.JSON);
			searchClient.index(indexRequest).get();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public D readDocument(String id, String type) {
		log.debug("Going to ES for data: " + id);
		try {
			GetRequest request = new GetRequest();
			request.id(id);
			request.type(type);
			request.index(ConfigHelper.getEsDataIndex());
			GetResponse res = searchClient.get(request).get();

			//log.debug("Result: " + res);
			//this.clazz = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			if(res.getSourceAsString() != null) {
				D doc = mapper.readValue(res.getSourceAsString(), (Class<D>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
				return doc;
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexNotFoundException e) {
			log.debug("Index not found --- creating index");
		}

		return null;
	}

	public void updateDocument(D doc) {
		try {
			String json = mapper.writeValueAsString(doc);
			
			IndexRequest indexRequest = new IndexRequest();
			indexRequest.index(ConfigHelper.getEsDataIndex());
			indexRequest.type(doc.getType());
			indexRequest.id(doc.getDocumentId());
			indexRequest.source(json, XContentType.JSON);
			searchClient.index(indexRequest).get();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void deleteDocument(String id) {
		// Do nothing for now.
	}

}
