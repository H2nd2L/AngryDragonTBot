package angryDragon.domain.wallet;

public class Wallet {
    private final String userId;
    private int cashValue;

    public Wallet(String userId) {
        this.userId = userId;
        this.cashValue = 100;
    }

    public String getUserId() {
        return userId;
    }

    public int getCashValue() {
        return cashValue;
    }

    public void setCashValue(int cashValue) {
        this.cashValue = cashValue;
    }
}
