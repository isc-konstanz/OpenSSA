/*
 * Copyright 2023-24 ISC Konstanz
 *
 * This file is part of OpenSSA.
 * For more information visit https://github.com/isc-konstanz/OpenSSA.
 *
 * OpenSSA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSSA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenSSA. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.esg.ic.ssa;

import static org.esg.ic.ssa.GenericAdapter.decodeEntity;
import static org.esg.ic.ssa.GenericAdapter.decodeResponse;
import static org.esg.ic.ssa.GenericAdapter.decodeResponseSet;
import static org.esg.ic.ssa.GenericAdapter.encodeEntity;
import static org.esg.ic.ssa.GenericAdapter.verifyResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.esg.ic.ssa.api.Binding;
import org.esg.ic.ssa.api.ResponseHandle;
import org.esg.ic.ssa.api.StartHandle;
import org.esg.ic.ssa.api.knowledge.AnswerKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.AskKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.KnowledgeBase;
import org.esg.ic.ssa.api.knowledge.PostKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;

public abstract class ServiceAdapter implements AutoCloseable {

    public static final String HASH = "hash";

    protected static final String SERVICE_PROPERTIES = "service.properties";

    protected static final int TIMEOUT = 29000;

    protected final GenericAdapter genericAdapter;

    protected final Properties serviceProperties;

    protected KnowledgeBase knowledgeBase;

    public ServiceAdapter(GenericAdapter genericAdapter, Properties serviceProperties)
    		throws GenericAdapterException {
    	this.genericAdapter = genericAdapter;
    	this.serviceProperties = serviceProperties;
    	this.readKnowledgeBase();
    	this.createKnowledgeBase();
    }

    public ServiceAdapter(GenericAdapter genericAdapter, String servicePropertiesFilename)
    		throws GenericAdapterException {
    	this.genericAdapter = genericAdapter;
    	try {
    		serviceProperties = new Properties();
			serviceProperties.load(getClass().getClassLoader().getResourceAsStream(servicePropertiesFilename));
			
		} catch (IOException e) {
			throw new GenericAdapterException("Unable to read service properties file: " + e.getMessage());
		}
    	this.readKnowledgeBase();
    	this.createKnowledgeBase();
    }

    public ServiceAdapter(GenericAdapter genericAdapter) throws GenericAdapterException {
    	this(genericAdapter, SERVICE_PROPERTIES);
    }

    public GenericAdapter getGenericAdapter() {
        return genericAdapter;
    }

    String readHash() {
        return serviceProperties.getProperty(String.format("%s.%s", getClass().getPackageName(), HASH));
    }

    String readKnowledgeBaseId() {
    	return serviceProperties.getProperty(String.format("%s.%s", getClass().getPackageName(), KnowledgeBase.ID), null);
    }

    String readKnowledgeBaseName() {
        return serviceProperties.getProperty(String.format("%s.%s", getClass().getPackageName(), KnowledgeBase.NAME));
    }

    String readKnowledgeBaseDescription() {
        return serviceProperties.getProperty(String.format("%s.%s", getClass().getPackageName(), KnowledgeBase.DESCRIPTION));
    }

    private void readKnowledgeBase() {
    	String knowleBaseId = readKnowledgeBaseId();
    	if (knowleBaseId != null && !knowleBaseId.isEmpty()) {
    		knowledgeBase = new KnowledgeBase(knowleBaseId, 
    				readKnowledgeBaseName(), 
    				readKnowledgeBaseDescription());
    	}
    }

    public boolean hasKnowledgeBase() {
        return knowledgeBase != null;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public void createKnowledgeBase() throws GenericAdapterException {
        if (!this.hasKnowledgeBase()) {
            String knowledgeBaseId = genericAdapter.registerServiceAdapter(this.readHash());
            this.setKnowledgeBase(new KnowledgeBase(knowledgeBaseId, 
            		this.readKnowledgeBaseName(), 
            		this.readKnowledgeBaseDescription()));
        }
        createKnowledgeBase(knowledgeBase);
    }

    @Override
    public void close() throws GenericAdapterException {
        deleteKnowledgeBase();
    }

    protected void createKnowledgeBase(KnowledgeBase knowledgeBase) throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/create");
            httpPost.setEntity(encodeEntity(knowledgeBase));
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected void deleteKnowledgeBase() throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
        	HttpDelete httpDelete = getGenericAdapter().buildHttpDelete("/smartconnector/delete");
            httpDelete.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());

            CloseableHttpResponse response = client.execute(httpDelete);
            verifyResponse(response);

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected String registerPostKnowledgeInteraction(PostKnowledgeInteraction knowledgeInteraction)
    		throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ki/register-post-react");
            httpPost.setEntity(encodeEntity(knowledgeInteraction));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected <B extends Binding> String postKnowledgeInteractionBinding(String knowledgeInteractionId, List<B> bindingSet)
    		throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/post");
            httpPost.setEntity(encodeEntity(bindingSet));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());
            httpPost.setHeader("KnowledgeInteractionId", knowledgeInteractionId);
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            return decodeEntity(response.getEntity());
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected String registerReactKnowledgeInteraction(ReactKnowledgeInteraction knowledgeInteraction)
    		throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ki/register-post-react");
            httpPost.setEntity(encodeEntity(knowledgeInteraction));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected StartHandle reactKnowledgeInteractionBinding() throws GenericAdapterException {
        return awaitKnowledgeInteractionBinding();
    }

    protected String registerAskKnowledgeInteraction(AskKnowledgeInteraction knowledgeInteraction)
    		throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ki/register-ask-answer");
            httpPost.setEntity(encodeEntity(knowledgeInteraction));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected <B extends Binding> List<B> askKnowledgeInteractionBinding(String knowledgeInteractionId, 
    		List<Binding> bindingSet, Class<B> resultType) throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ask");
            httpPost.setEntity(encodeEntity(bindingSet));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());
            httpPost.setHeader("KnowledgeInteractionId", knowledgeInteractionId);
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
			@SuppressWarnings("unchecked")
			ResponseHandle<B> responseHandle = decodeResponse(response, ResponseHandle.class);
			
			// TODO: Handle exchangeInfo for "FAILED" status
            return responseHandle.getResultBindingSet();
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected String registerAnswerKnowledgeInteraction(AnswerKnowledgeInteraction knowledgeInteraction)
    		throws GenericAdapterException {        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ki/register-ask-answer");
            httpPost.setEntity(encodeEntity(knowledgeInteraction));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected StartHandle answerKnowledgeInteractionBinding() throws GenericAdapterException {
        return awaitKnowledgeInteractionBinding();
    }

    private StartHandle awaitKnowledgeInteractionBinding() throws GenericAdapterException	{
    	RequestConfig requestConfig = RequestConfig.custom()
    		    .setConnectionRequestTimeout(TIMEOUT)
    		    .setConnectTimeout(TIMEOUT)
    		    .setSocketTimeout(TIMEOUT)
    		    .build();
        
        try (CloseableHttpClient client = HttpClientBuilder.create()
        	    .setDefaultRequestConfig(requestConfig)
        	    .build()) {
        	HttpGet httpGet = getGenericAdapter().buildHttpGet("/smartconnector/handle/start");
            httpGet.setHeader("KnowledgeBaseId", getKnowledgeBase().getId());
            //httpGet.setHeader("KnowledgeInteractionId", knowledgeInteractionId);
            
            CloseableHttpResponse response = client.execute(httpGet);
            verifyResponse(response);
            
            return decodeResponse(response, StartHandle.class);

        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            throw new GenericAdapterTimeoutException(e);
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

}
