package angryDragon.business.handlers.userSession;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
    private SessionState state;
    private final Map<KeyPair, String> data;

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

    public void putData(KeyPair keyPair, String value) {
        data.put(keyPair, value);
    }

    public String getData(KeyPair keyPair) {
        return data.get(keyPair);
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
        ADD_USER_WAITING_NAME,

        // Добавление питомца
        ADD_PET_WAITING_PET_NAME,

        // Использование предмета
        USE_ITEM_WAITING_ITEM_ID,

        // Покупка предмета
        BUY_ITEM_WAITING_ITEM_ID,

        // Продажа предмета
        SELL_ITEM_WAITING_ITEM_ID,

        // Добавление предмета
        ADD_ITEM_WAITING_ID,
        ADD_ITEM_WAITING_NAME,
        ADD_ITEM_WAITING_PRICE,
        ADD_ITEM_WAITING_STATUS_TYPE,
        ADD_ITEM_WAITING_STATUS_VALUE,

        // Добавление предмета в магазин
        ADD_ITEM_TO_SHOP_WAITING_ID
    }
}
