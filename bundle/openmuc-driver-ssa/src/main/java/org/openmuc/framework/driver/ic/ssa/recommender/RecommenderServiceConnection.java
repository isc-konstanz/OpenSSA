/*
 * Copyright 2011-2022 Fraunhofer ISE
 *
 * This file is part of OpenMUC.
 * For more information visit http://www.openmuc.org
 *
 * OpenMUC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenMUC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenMUC.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openmuc.framework.driver.ic.ssa.recommender;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import org.esg.ic.ssa.GenericAdapter;
import org.esg.ic.ssa.GenericAdapterException;
import org.esg.ic.ssa.ServiceAdapterSettings;
import org.esg.ic.ssa.recommender.RecommenderAskInteraction;
import org.esg.ic.ssa.recommender.RecommenderServiceAdapter;
import org.esg.ic.ssa.recommender.data.Recommendation;
import org.esg.ic.ssa.recommender.data.RiskEvaluation;
import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.ChannelScanInfo;
import org.openmuc.framework.config.ScanException;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.IntValue;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.driver.spi.ChannelRecordContainer;
import org.openmuc.framework.driver.spi.ChannelValueContainer;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.RecordsReceivedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecommenderServiceConnection extends RecommenderServiceAdapter implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(RecommenderServiceConnection.class);

    private final ZoneId timezone;

    private final String countryCode;

    public RecommenderServiceConnection(GenericAdapter genericAdapter, ServiceAdapterSettings settings)
            throws GenericAdapterException {
        super(genericAdapter, settings);
        
        // TODO: Make configurable
        this.timezone = ZoneId.of("CET");
        this.countryCode = "DE";
        
        this.register();
    }

    @Override
    public List<ChannelScanInfo> scanForChannels(String settings)
            throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startListening(List<ChannelRecordContainer> containers, RecordsReceivedListener recordListener)
            throws UnsupportedOperationException, ConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object read(List<ChannelRecordContainer> containers, Object containerListHandle, String samplingGroup)
            throws UnsupportedOperationException, ConnectionException {
        try {
            RecommenderAskInteraction interaction = registerAskKnowledgeInteraction(countryCode);
            
            ZonedDateTime currentDateTime = ZonedDateTime.now(timezone);
            ZonedDateTime startDateTime = currentDateTime.truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDateTime = startDateTime.plusDays(1).minusHours(1);
            
            List<Recommendation> recommendations = interaction.ask(startDateTime, endDateTime);
            
            for (ChannelRecordContainer channelContainer : containers) {
                ZonedDateTime channelDateTime;
                if (channelContainer.getChannelAddress().isEmpty()) {
                    channelDateTime = currentDateTime.truncatedTo(ChronoUnit.HOURS);
                }
                else {
                    Integer hour = Integer.parseInt(channelContainer.getChannelAddress());
                    channelDateTime = startDateTime.plusHours(hour);
                }
                try {
                    Recommendation recommendation = recommendations.stream()
                            .filter(r -> r.getDatetime().isEqual(channelDateTime))
                            .findFirst().orElseThrow();

                    channelContainer.setRecord(decodeRecord(recommendation));
                    
                } catch (NoSuchElementException e) {
                    channelContainer.setRecord(new Record(Flag.DRIVER_ERROR_CHANNEL_TEMPORARILY_NOT_ACCESSIBLE));
                }
            }
        } catch (GenericAdapterException e) {
            for (ChannelRecordContainer channelContainer : containers) {
                channelContainer.setRecord(new Record(Flag.DRIVER_ERROR_READ_FAILURE));
            }
            throw new ConnectionException("Error during ask interaction with recommender service: " + e.getMessage());
        }
        return null;
    }

    private Record decodeRecord(Recommendation recommendation) {
        if (recommendation.getRiskLevel() == null || 
                recommendation.getRiskEvaluation() == RiskEvaluation.NOT_AVAILABLE) {
            return new Record(Flag.DRIVER_ERROR_CHANNEL_TEMPORARILY_NOT_ACCESSIBLE);
        }
        int riskLevel = recommendation.getRiskLevel().getLevel();
        
        if (recommendation.getRiskEvaluation() == RiskEvaluation.HEALTHY && riskLevel != 0) {
            logger.warn("Inconsistend or invalid recommendation received at {} for \"{}\": {}",
                    recommendation.getDatetime(), recommendation.getRiskEvaluation(), recommendation.getRiskLevel());
            
            return new Record(Flag.DRIVER_ERROR_READ_FAILURE);
        }
        if (recommendation.getRiskEvaluation() == RiskEvaluation.DECREASE) {
            riskLevel *= -1;
        }
        return new Record(new IntValue(riskLevel), System.currentTimeMillis(), Flag.VALID);
    }

    @Override
    public Object write(List<ChannelValueContainer> containers, Object containerListHandle)
            throws UnsupportedOperationException, ConnectionException {
        // TODO: Implement POST knowledgeInteraction here
        throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect() {
        try {
            close();
            
        } catch (GenericAdapterException e) {
            logger.warn("Unknown error while closing service adapter: {}", e.getMessage());
        }
    }
}
