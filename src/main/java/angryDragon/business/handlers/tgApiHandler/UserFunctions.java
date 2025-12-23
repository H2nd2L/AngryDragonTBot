package angryDragon.business.handlers.tgApiHandler;

import angryDragon.business.domain.user.User;
import angryDragon.business.domain.wallet.Wallet;
import angryDragon.components.repository.RepositoryComponent;

public class UserFunctions {
    private final RepositoryComponent repositoryComponent;

    public UserFunctions(RepositoryComponent repositoryComponent) {
        this.repositoryComponent = repositoryComponent;
    }

    /**
     * Добавление пользователя
     * @param id ID пользователя
     * @param name Имя пользователя
     */
    String addUserFromSession(String id, String name) {
        try {
            if (repositoryComponent.getUsersRepository().findById(id) != null) {
                return "Вы уже создали свой профиль";
            }

            User user = new User(id, name);
            repositoryComponent.getUsersRepository().addUser(user);
            Wallet wallet = new Wallet(id);
            repositoryComponent.getWalletsRepository().addWallet(wallet);
            return "✓ Пользователь успешно добавлен:\n" +
                    "     ID: " + id + "\n" +
                    "     Имя: " + name + "\n" +
                    "     Баланс: " + wallet.getCashValue();
        } catch (Exception e) {
            return "✗ Ошибка при добавлении пользователя:\n" + e.getMessage();
        }
    }

    /**
     * Показ баланса пользователя
     * @param chatId ID пользователя
     * @return Баланс пользователя
     */
    String walletFromSession(long chatId) {
        String userId = "U" + String.valueOf(chatId);
        User user = repositoryComponent.getUsersRepository().findById(userId);

        if (user == null) {
            return "Ошибка: не существует такого пользователя";
        }

        try {
            int cash = repositoryComponent.getWalletsRepository().getUserCashValue(userId);
            return "  Баланс пользователя: " + cash;
        } catch (Exception e) {
            return "✗ Ошибка при показе баланса:\n" + e.getMessage();
        }
    }
}
