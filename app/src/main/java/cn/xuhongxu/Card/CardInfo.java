package cn.xuhongxu.Card;

/**
 * Created by xuhon on 2016/9/19.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class CardInfo {
    private String name;
    private String id;
    private String cardId;
    private String balance;
    private String transition;
    private String status;
    private String frozen;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    @Override
    public String toString() {
        return "CardInfo{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", cardId='" + cardId + '\'' +
                ", balance='" + balance + '\'' +
                ", transition='" + transition + '\'' +
                ", status='" + status + '\'' +
                ", frozen='" + frozen + '\'' +
                '}';
    }
}
