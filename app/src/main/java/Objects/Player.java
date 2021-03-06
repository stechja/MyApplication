package Objects;

import java.io.Serializable;
import java.util.ArrayList;


public class Player implements Serializable {
    private String ID;
    private ArrayList<Integer> payout_history;
    private String type;
    private Integer cash;
    private Integer value;

    public Integer getCash() {
        return cash;
    }

    public void setCash(Integer cash) {
        this.cash = cash;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Player() {

    }

    public Player(String ID) {
        this.ID = ID;
        this.payout_history = new ArrayList<Integer>();
        this.cash = 100;
        this.value = 100;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArrayList<Integer> getPayout_history() {
        return payout_history;
    }

    public void setPayout_history(ArrayList<Integer> payout_history) {
        this.payout_history = payout_history;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
