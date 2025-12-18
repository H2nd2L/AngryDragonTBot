package angryDragon.business.handlers;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
    private SessionState state;
    private final Map<String, String> data;

    public UserSession() {
        this.state = SessionState.IDLE;
        this.data = new HashMap<>();
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public void putData(String key, String value) {
        data.put(key, value);
    }

    public String getData(String key) {
        return data.get(key);
    }

    public void clearData() {
        data.clear();
    }

    public void reset() {
        this.state = SessionState.IDLE;
        this.data.clear();
    }

    /**
     * Перечисление возможных состояний сессии
     */
    public enum SessionState {
        // Нет активного диалога
        IDLE,

        // Добавление пользователя
        ADD_USER_WAITING_ID,
        ADD_USER_WAITING_NAME,

        // Показ баланса пользователя
        SHOW_WALLET_WAITING_ID,

        // Добавление питомца
        ADD_PET_WAITING_USER_ID,
        ADD_PET_WAITING_PET_ID,
        ADD_PET_WAITING_PET_NAME,

        // Показ статуса питомца
        SHOW_PET_STATUS_WAITING_ID,

        // Показ инвентаря питомца
        SHOW_INVENTORY_WAITING_ID,

        // Использование предмета
        USE_ITEM_WAITING_PET_ID,
        USE_ITEM_WAITING_ITEM_ID,

        // Покупка предмета
        BUY_ITEM_WAITING_USER_ID,
        BUY_ITEM_WAITING_ITEM_ID,

        // Продажа предмета
        SELL_ITEM_WAITING_USER_ID,
        SELL_ITEM_WAITING_ITEM_ID,

        // Добавление предмета
        ADD_ITEM_WAITING_ID,
        ADD_ITEM_WAITING_NAME,
        ADD_ITEM_WAITING_PRICE,
        ADD_ITEM_WAITING_STATUS_TYPE,
        ADD_ITEM_WAITING_STATUS_VALUE,

        // Добавление предмета в магазин
        ADD_SHOP_WAITING_ID
    }
}
