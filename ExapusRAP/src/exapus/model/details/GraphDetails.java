package exapus.model.details;

public enum GraphDetails {
    TOP_LEVEL_TAGS, GROUPED_PACKAGES, TOP_LEVEL_TYPES, METHODS;

    public static GraphDetails defaultValue() {
        return GROUPED_PACKAGES;
    }

    public static GraphDetails[] supportedDetails() {
        return GraphDetails.class.getEnumConstants();
    }
}
