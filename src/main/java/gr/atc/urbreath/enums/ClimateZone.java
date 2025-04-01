package gr.atc.urbreath.enums;

/*
 * Enum for Climate Zone
 */
public enum ClimateZone {
    ATLANTIC("Atlantic"),
    BOREAL("Boreal"),
    CONTINENTAL("Continental"),
    MEDITERRANEAN("Mediterranean");

    public final String zone;

    ClimateZone(String zone) {
        this.zone = zone;
    }

    public static ClimateZone fromString(String value) {
        for (ClimateZone climateZone : ClimateZone.values()) {
            if (climateZone.name().equalsIgnoreCase(value)) {
                return climateZone;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return zone;
    }
}
