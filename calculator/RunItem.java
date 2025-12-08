package calculator;

public class RunItem {
    private String name;
    private int quantity;

    RunItem(String itemName, int itemQuantity) {
        this.name = itemName;
        this.quantity = itemQuantity;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}