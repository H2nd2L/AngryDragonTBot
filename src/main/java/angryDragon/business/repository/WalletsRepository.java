package angryDragon.business.repository;

import angryDragon.business.domain.wallet.Wallet;

public interface WalletsRepository {
    void addWallet(Wallet wallet);

    int getUserCashValue(String userId);

    void setUserCashValue(String userId, int newCashAmount);
}