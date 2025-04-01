package gr.atc.urbreath.enums;

public enum NbsStatus {
    TO_BE_IMPLEMENTED("To be Implemented"),
    UNDER_IMPLEMENTATION("Under Implementation"),
    IMPLEMENTED("Implemented");

    public final String status;

    NbsStatus(String status) {
        this.status = status;
    }

    public NbsStatus fromString(String value) {
        for (NbsStatus nbsStatus : NbsStatus.values()) {
            if (nbsStatus.name().equalsIgnoreCase(value)) {
                return nbsStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return status;
    }
}
