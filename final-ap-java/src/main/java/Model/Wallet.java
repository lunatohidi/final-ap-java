package Model;

public class Wallet {
    private double balance = 0.0;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            this.balance = 0;
        } else {
            this.balance = balance;
        }
    }

    // شارژ کیف پول
    public void charge(double amount) {
        if (amount <= 0) {
            return;
        }
        balance += amount;
    }

    // پرداخت اگر موجودی کافی باشد
    public boolean pay(double amount) {
        if (amount <= 0) {
            return true;
        }
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
