?node ic-data:hasDataPoint ?data .
?data ic-data:containsQuantity ?quantity .
?quantity rdf:type om:Quantity .
?quantity om:hasValue ?stimulus .
?stimulus rdf:type om:Measure .
?stimulus om:hasNumericalValue ?value .
?stimulus om:hasUnit ?unit .
?unit a om:PercentageUnit .
?data saref:hasTimestamp ?timestamp .
?timestamp rdf:type xsd:dateTime .