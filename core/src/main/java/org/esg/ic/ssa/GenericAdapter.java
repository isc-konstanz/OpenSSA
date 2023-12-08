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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.esg.ic.ssa.api.Binding;
import org.esg.ic.ssa.api.BindingSet.BindingDeserializerModifier;
import org.esg.ic.ssa.api.BindingSetHandle;
import org.esg.ic.ssa.api.LoginForm;
import org.esg.ic.ssa.api.RegisterAdapterForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class GenericAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GenericAdapter.class);

    private static final String DEFAULT_HOST = "http://localhost";
    private static final int DEFAULT_PORT = 9090;

    private final String genericAdapterURL;

    public GenericAdapter() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public GenericAdapter(String genericAdapterHost, int genericAdapterPort) {
        this.genericAdapterURL = genericAdapterHost + ":" + genericAdapterPort;
    }

    public String getGenericAdapterURL() {
        return genericAdapterURL;
    }

    public void login(String user, String password) throws GenericAdapterException {
        LoginForm loginForm = new LoginForm(user, password);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
        	HttpPost httpPost = buildHttpPost("/servicestore/login");
            httpPost.setEntity(encodeEntity(loginForm));
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    public String registerServiceAdapter(String serviceAdapterBinding) throws GenericAdapterException {
        RegisterAdapterForm registerForm = new RegisterAdapterForm(serviceAdapterBinding);
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
        	HttpPost httpPost = buildHttpPost("/servicestore/adapter/register");
            httpPost.setEntity(encodeEntity(registerForm));
            
            CloseableHttpResponse response = client.execute(httpPost);
            verifyResponse(response);
            
            return decodeEntity(response.getEntity());

        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    HttpGet buildHttpGet(String uri) throws GenericAdapterException {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.genericAdapterURL + uri);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Accept", "*/*");
            httpGet.setHeader("Content-type", "application/json");
            
            return httpGet;
            
        } catch (URISyntaxException e) {
            throw new GenericAdapterException(e);
        }
    }

    HttpPost buildHttpPost(String uri) throws GenericAdapterException {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.genericAdapterURL + uri);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");
            
            return httpPost;
            
        } catch (URISyntaxException e) {
            throw new GenericAdapterException(e);
        }
    }

    HttpDelete buildHttpDelete(String uri) throws GenericAdapterException {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.genericAdapterURL + uri);
            HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
            httpDelete.setHeader("Accept", "*/*");
            httpDelete.setHeader("Content-type", "application/json");
            
            return httpDelete;
            
        } catch (URISyntaxException e) {
            throw new GenericAdapterException(e);
        }
    }

    static void verifyResponse(CloseableHttpResponse response) throws GenericAdapterConnectionException {
        if (response == null) {
            throw new GenericAdapterConnectionException("No response received");
        }
        StatusLine status = response.getStatusLine();
        if (HttpStatus.SC_ACCEPTED == status.getStatusCode()) {
            throw new GenericAdapterTimeoutException(status.getReasonPhrase());
        }
        if (HttpStatus.SC_OK != status.getStatusCode()) {
            throw new GenericAdapterConnectionException(status.getReasonPhrase());
        }
    }

    static <T> T decodeResponse(CloseableHttpResponse response, 
            Class<T> responseType) throws GenericAdapterException {
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        return decodeResponse(response, typeFactory.constructType(responseType));
    }

    static <T> T decodeResponse(CloseableHttpResponse response, 
            JavaType responseType) throws GenericAdapterException {
        try {
        	return new ObjectMapper()
		    		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		    		.readValue(decodeEntity(response.getEntity()), responseType);
            
        } catch (JsonProcessingException e) {
            throw new GenericAdapterException(e);
        }
    }

    static Map<String, String> decodeResponseSet(CloseableHttpResponse response) throws GenericAdapterException {
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        return decodeResponse(response, typeFactory.constructMapType(HashMap.class, String.class, String.class));
    }

    static <B extends Binding, H extends BindingSetHandle<B>> H decodeHandle(CloseableHttpResponse response, 
    		Class<H> handleType, Class<B> bindingType) throws GenericAdapterException {
    	SimpleModule module = new SimpleModule()
    			.setDeserializerModifier(new BindingDeserializerModifier<B>(bindingType));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        try {
        	return objectMapper
		    		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		    		.readValue(decodeEntity(response.getEntity()), handleType);

        } catch (JsonProcessingException e) {
            throw new GenericAdapterException(e);
        }
    }

    static String decodeEntity(HttpEntity entity) throws GenericAdapterException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuilder entityJson = new StringBuilder();
            String entityLine;
            while ((entityLine = reader.readLine()) != null) {
                entityJson.append(entityLine);
            }
            logger.debug("Decode entity: {}", entityJson);
            return entityJson.toString();
            
        } catch (IOException e) {
            throw new GenericAdapterConnectionException(e);
        }
    }

    static StringEntity encodeEntity(Object entity) throws GenericAdapterException {
        String entityJson = encodeJson(entity);
        logger.debug("Encode entity: {}", entityJson);
        return new StringEntity(entityJson, "UTF-8");
    }

    static String encodeJson(Object object) throws GenericAdapterException {
        try {
            return new ObjectMapper().writeValueAsString(object);

        } catch (JsonProcessingException e) {
            throw new GenericAdapterException(e);
        }
    }

}
