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
package org.esg.ic.ssa.api.knowledge;

public class KnowledgeBase {

    public final static String ID = "knowledgeBaseId";
    public final static String NAME = "knowledgeBaseName";
    public final static String DESCRIPTION = "knowledgeBaseDescription";

    public String knowledgeBaseId;
    public String knowledgeBaseName;
    public String knowledgeBaseDescription;
    private boolean reasonerEnabled;

    public KnowledgeBase(String knowledgeBaseId, String knowledgeBaseName, String knowledgeBaseDescription) {
        this.knowledgeBaseId = knowledgeBaseId;
        this.knowledgeBaseName = knowledgeBaseName;
        this.knowledgeBaseDescription = knowledgeBaseDescription;
    }

    public KnowledgeBase(String knowledgeBaseId, String knowledgeBaseName, String knowledgeBaseDescription, int leaseRenewalTime, boolean reasonerEnabled) {
        this.knowledgeBaseId = knowledgeBaseId;
        this.knowledgeBaseName = knowledgeBaseName;
        this.knowledgeBaseDescription = knowledgeBaseDescription;
        this.reasonerEnabled = reasonerEnabled;
    }

    public String getId() {
        return knowledgeBaseId;
    }

    public void setId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public String getName() {
        return knowledgeBaseName;
    }

    public void setName(String knowledgeBaseName) {
        this.knowledgeBaseName = knowledgeBaseName;
    }

    public String getDescription() {
        return knowledgeBaseDescription;
    }

    public void setDescription(String knowledgeBaseDescription) {
        this.knowledgeBaseDescription = knowledgeBaseDescription;
    }

    public boolean isReasonerEnabled() {
        return reasonerEnabled;
    }

    public void setReasonerEnabled(boolean reasonerEnabled) {
        this.reasonerEnabled = reasonerEnabled;
    }
}
