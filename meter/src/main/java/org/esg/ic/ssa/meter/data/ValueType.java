package org.esg.ic.ssa.meter.data;

public enum ValueType {

    POWER("<https://saref.etsi.org/core/Power>", "<http://www.ontology-of-units-of-measure.org/resource/om-2/kilowatt>"),
    ENERGY("<https://saref.etsi.org/core/Energy>", "<http://www.ontology-of-units-of-measure.org/resource/om-2/kilowatthour>");

    private final String type;
    private final String unit;

    private ValueType(String type, String unit) {
        this.type = type;
        this.unit = unit;
    }

    public String getSuffix() {
    	return String.format("_%s", name().toLowerCase());
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

}
