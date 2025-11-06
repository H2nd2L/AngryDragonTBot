package angryDragon.repository.impl;

import angryDragon.domain.wallet.Wallet;
import angryDragon.repository.WalletsRepository;

import java.util.ArrayList;
import java.util.List;

public class WalletsRepositoryImpl implements WalletsRepository {
    private final List<Wallet> wallets = new ArrayList<>();

    @Override
    public void addWallet(Wallet wallet){
        wallets.add(wallet);
    }

    @Override
    public int getUserCashValue(String userId){
        for (Wallet wallet : wallets){
            if (wallet.getUserId().equals(userId)){
                return wallet.getCashValue();
            }
        }
        return -1;
    }

    @Override
    public String setUserCashValue(String userId, int newCashAmount){
        for (Wallet wallet : wallets){
            if (wallet.getUserId().equals(userId)){
                wallet.setCashValue(newCashAmount);
                return "User got his money.";
            }
        }
        return "User doesn't exist.";
    }
}
