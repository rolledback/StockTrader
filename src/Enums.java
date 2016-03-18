enum RESULT {
    FAILURE(0),
    SUCCESS(1),
    INVALID_TRADER(2),
    INVALID_STOCK(3),
    INSUFFICIENT_FUNDS(4),
    INSUFFICIENT_SUPPLY(5);

    private final int value;

    RESULT(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public String toString() { return Integer.toString(this.getValue()); }
}
