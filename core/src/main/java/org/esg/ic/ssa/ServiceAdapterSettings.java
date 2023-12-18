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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.esg.ic.ssa.api.knowledge.KnowledgeBase;

public class ServiceAdapterSettings {

    public static final String HASH = "hash";
    public final static String KNOWLEDGE_BASE_ID = "knowledgeBaseId";
    public final static String KNOWLEDGE_BASE_NAME = "knowledgeBaseName";
    public final static String KNOWLEDGE_BASE_DESCRIPTION = "knowledgeBaseDescription";

    private final String hash;

    private final KnowledgeBase knowledgeBase;

    public ServiceAdapterSettings(String hash, KnowledgeBase knowledgeBase) {
    	this.hash = hash;
    	this.knowledgeBase = knowledgeBase;
    }

    public ServiceAdapterSettings(
    		String hash, 
    		String knowledgeBaseId, 
    		String knowledgeBaseName, 
    		String knowledgeBaseDescription) {
    	this(hash, new KnowledgeBase(knowledgeBaseId, knowledgeBaseName, knowledgeBaseDescription));
    }

    public String getHash() {
		return hash;
	}

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	public boolean hasKnowledgeBaseId() {
		return knowledgeBase.getKnowledgeBaseId() != null;
	}

	public String getKnowledgeBaseId() {
		return knowledgeBase.getKnowledgeBaseId();
	}

	public void setKnowledgeBaseId(String knowledgeBaseId) {
		this.knowledgeBase.setKnowledgeBaseId(knowledgeBaseId);
	}

	public String getKnowledgeBaseName() {
		return knowledgeBase.getKnowledgeBaseName();
	}

	public String getKnowledgeBaseDescription() {
		return knowledgeBase.getKnowledgeBaseDescription();
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HASH).append(" = ").append(getHash()).append("\n");
        sb.append(KNOWLEDGE_BASE_ID).append(" = ").append(getKnowledgeBaseId()).append("\n");
        sb.append(KNOWLEDGE_BASE_NAME).append(" = ").append(getKnowledgeBaseName()).append("\n");
        sb.append(KNOWLEDGE_BASE_DESCRIPTION).append(" = ").append(getKnowledgeBaseDescription()).append("\n");
        return sb.toString();
    }

    public static <S extends ServiceAdapter> ServiceAdapterSettings ofProperties(
    		Class<S> propertiesType, 
    		String propertiesFileName) throws GenericAdapterException {
    	Properties properties = new Properties();
    	try {
    		properties.load(propertiesType.getClassLoader().getResourceAsStream(propertiesFileName));
			
		} catch (IOException e) {
			throw new GenericAdapterException("Unable to read service properties file: " + e.getMessage());
		}
    	return ofProperties(properties);
    }

    public static ServiceAdapterSettings ofProperties(Path propertiesPath) 
    		throws GenericAdapterException {
    	Properties properties = new Properties();
    	try {
        	properties.load(new FileInputStream(propertiesPath.toFile()));
			
		} catch (IOException e) {
			throw new GenericAdapterException("Unable to read service properties file: " + e.getMessage());
		}
    	return ofProperties(properties);
    }

    public static ServiceAdapterSettings ofProperties(Properties properties) {
    	return new ServiceAdapterSettings(
    			properties.getProperty(HASH),
    			properties.getProperty(KNOWLEDGE_BASE_ID, null),
                properties.getProperty(KNOWLEDGE_BASE_NAME),
                properties.getProperty(KNOWLEDGE_BASE_DESCRIPTION));
    }
}
