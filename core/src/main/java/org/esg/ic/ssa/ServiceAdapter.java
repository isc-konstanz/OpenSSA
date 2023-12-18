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
import static org.esg.ic.ssa.GenericAdapter.decodeHandle;
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
import org.esg.ic.ssa.api.BindingSet;
import org.esg.ic.ssa.api.ResponseHandle;
import org.esg.ic.ssa.api.SendHandle;
import org.esg.ic.ssa.api.StartHandle;
import org.esg.ic.ssa.api.knowledge.AnswerKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.AskKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.KnowledgeBase;
import org.esg.ic.ssa.api.knowledge.PostKnowledgeInteraction;
import org.esg.ic.ssa.api.knowledge.ReactKnowledgeInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceAdapter implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAdapter.class);

    protected static final String SERVICE_PROPERTIES = "service.properties";

    protected static final int TIMEOUT = 29000;

    protected final GenericAdapter genericAdapter;

    protected final ServiceAdapterSettings settings;

    protected ServiceAdapter(GenericAdapter genericAdapter, ServiceAdapterSettings settings)
    		throws GenericAdapterException {
    	this.genericAdapter = genericAdapter;
    	this.settings = settings;
    	this.createKnowledgeBase();
    }

    protected ServiceAdapter(GenericAdapter genericAdapter, Properties serviceProperties)
    		throws GenericAdapterException {
    	this(genericAdapter, ServiceAdapterSettings.ofProperties(serviceProperties));
    }

    protected ServiceAdapter(GenericAdapter genericAdapter, String servicePropertiesFilename)
    		throws GenericAdapterException {
    	this.genericAdapter = genericAdapter;
    	this.settings = ServiceAdapterSettings.ofProperties(getClass(), servicePropertiesFilename);
    	this.createKnowledgeBase();
    }

    public ServiceAdapter(GenericAdapter genericAdapter) throws GenericAdapterException {
    	this(genericAdapter, SERVICE_PROPERTIES);
    }

    public GenericAdapter getGenericAdapter() {
        return genericAdapter;
    }

    public KnowledgeBase getKnowledgeBase() {
        return settings.getKnowledgeBase();
    }

    protected void createKnowledgeBase() throws GenericAdapterException {
        if (!settings.hasKnowledgeBaseId()) {
            String knowledgeBaseId = genericAdapter.registerServiceAdapter(settings.getHash());
            settings.setKnowledgeBaseId(knowledgeBaseId);
        }
        createKnowledgeBase(settings.getKnowledgeBase());
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
            httpDelete.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());

            CloseableHttpResponse response = client.execute(httpDelete);
            verifyResponse(response);

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    @Override
    public void close() throws GenericAdapterException {
        deleteKnowledgeBase();
    }

    protected String registerPostKnowledgeInteraction(PostKnowledgeInteraction knowledgeInteraction)
    		throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ki/register-post-react");
            httpPost.setEntity(encodeEntity(knowledgeInteraction));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected <B extends Binding> String postKnowledgeInteractionBinding(String knowledgeInteractionId,
    		List<B> bindingSet) throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/post");
            httpPost.setEntity(encodeEntity(bindingSet));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
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
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected <B extends Binding> BindingSet<B> reactKnowledgeInteractionBinding(String knowledgeInteractionId,
    		List<B> bindingSet, Class<B> bindingType) throws GenericAdapterException {
		StartHandle<B> startHandle = startKnowledgeInteractionHandle(bindingType);
		SendHandle<B> sendHandle = SendHandle.ofStartHandle(startHandle);
		sendHandle.setBindingSet(bindingSet);

		String sendHandleId = String.format("%s:%s", knowledgeInteractionId, sendHandle.getHandleRequestId());
        Thread sendThread = new Thread("SmartConnector-send-"+sendHandleId) {
            @Override
            public void run() {
        		try {
					sendKnowledgeInteractionHandle(knowledgeInteractionId, sendHandle);
					
				} catch (GenericAdapterException e) {
					logger.warn("Error sending handle {}", sendHandleId);
				}
            }
        };
        sendThread.start();
		
        return startHandle.getBindingSet();
    }

    protected String registerAskKnowledgeInteraction(AskKnowledgeInteraction knowledgeInteraction)
    		throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ki/register-ask-answer");
            httpPost.setEntity(encodeEntity(knowledgeInteraction));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected <B extends Binding> BindingSet<B> askKnowledgeInteractionBinding(String knowledgeInteractionId, 
    		List<B> bindingSet, Class<B> resultType) throws GenericAdapterException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ask");
            httpPost.setEntity(encodeEntity(bindingSet));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
            httpPost.setHeader("KnowledgeInteractionId", knowledgeInteractionId);
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
			@SuppressWarnings("unchecked")
			ResponseHandle<B> responseHandle = decodeHandle(response, ResponseHandle.class, resultType);
			
			// TODO: Handle exchangeInfo for "FAILED" status
            return responseHandle.getBindingSet();
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected String registerAnswerKnowledgeInteraction(AnswerKnowledgeInteraction knowledgeInteraction)
    		throws GenericAdapterException {        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/ki/register-ask-answer");
            httpPost.setEntity(encodeEntity(knowledgeInteraction));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            Map<String, String> responseSet = decodeResponseSet(response);
            return responseSet.get("knowledgeInteractionId");
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    protected <B extends Binding> void answerKnowledgeInteractionBinding(String knowledgeInteractionId,
    		List<B> bindingSet, Class<B> bindingType) throws GenericAdapterException {
		StartHandle<B> startHandle = startKnowledgeInteractionHandle(bindingType);
		SendHandle<B> sendHandle = SendHandle.ofStartHandle(startHandle);
		sendHandle.setBindingSet(bindingSet);

		String sendHandleId = String.format("%s:%s", knowledgeInteractionId, sendHandle.getHandleRequestId());
        Thread sendThread = new Thread("SmartConnector-send-"+sendHandleId) {
            @Override
            public void run() {
        		try {
					sendKnowledgeInteractionHandle(knowledgeInteractionId, sendHandle);
					
				} catch (GenericAdapterException e) {
					logger.warn("Error sending handle {}", sendHandleId);
				}
            }
        };
        sendThread.start();
    }

    @SuppressWarnings("unchecked")
	private <B extends Binding> StartHandle<B> startKnowledgeInteractionHandle(Class<B> bindingType) 
    		throws GenericAdapterException	{
    	RequestConfig requestConfig = RequestConfig.custom()
    		    .setConnectionRequestTimeout(TIMEOUT)
    		    .setConnectTimeout(TIMEOUT)
    		    .setSocketTimeout(TIMEOUT)
    		    .build();
        
        try (CloseableHttpClient client = HttpClientBuilder.create()
        	    .setDefaultRequestConfig(requestConfig)
        	    .build()) {
        	HttpGet httpGet = getGenericAdapter().buildHttpGet("/smartconnector/handle/start");
            httpGet.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
            
            CloseableHttpResponse response = client.execute(httpGet);
            verifyResponse(response);

			// TODO: Handle exchangeInfo for "FAILED" status
			return decodeHandle(response, StartHandle.class, bindingType);

        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            throw new GenericAdapterTimeoutException(e);
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    private <B extends Binding> void sendKnowledgeInteractionHandle(String knowledgeInteractionId,
    		SendHandle<B> sendHandle) throws GenericAdapterException {    	
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = getGenericAdapter().buildHttpPost("/smartconnector/handle/send");
            httpPost.setEntity(encodeEntity(sendHandle));
            httpPost.setHeader("KnowledgeBaseId", getKnowledgeBase().getKnowledgeBaseId());
            httpPost.setHeader("KnowledgeInteractionId", knowledgeInteractionId);
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);

        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            throw new GenericAdapterTimeoutException(e);
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

}
