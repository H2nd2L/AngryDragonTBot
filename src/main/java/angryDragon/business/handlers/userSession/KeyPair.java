package angryDragon.business.handlers.userSession;

import java.util.Objects;

public class KeyPair {
    private final long chatId;
    private final String key;

    public KeyPair(long chatId, String key) {
        this.chatId = chatId;
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyPair keyPair = (KeyPair) o;
        return chatId == keyPair.chatId && Objects.equals(key, keyPair.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, key);
    }
}
