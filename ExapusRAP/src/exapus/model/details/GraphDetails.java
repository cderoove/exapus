package exapus.model.details;

public enum GraphDetails {
    GROUPED_PACKAGES, TOP_LEVEL_TYPES;

    public static GraphDetails defaultValue() {
        return GROUPED_PACKAGES;
    }

    public static GraphDetails[] supportedDetails() {
        return GraphDetails.class.getEnumConstants();
    }
}
