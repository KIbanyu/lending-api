package lendingapi.utils;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
public enum LendingApiEnums {
    LIMIT("loanLimit"),
    DATA("data"),
    STATUS("status"),
    ACTIVE("ACTIVE"),
    KE("KE"),
    TOKEN("accessToken"),
    MESSAGE("message");


    public final String label;

    private LendingApiEnums(String label) {
        this.label = label;
    }
}
