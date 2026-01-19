package Model;

public class WalletTopUpRequest {
    // شناسه درخواست
    private int id;
    // نام کاربری درخواست کننده
    private String username;
    // مبلغ شارژ درخواستی
    private double amount;
    // وضعیت درخواست (معلق/تایید شده/رد شده)
    private String status;
    // پیام ادمین (توضیحات مربوط به تایید یا رد)
    private String adminMessage;

    /**
     * سازنده کلاس برای ایجاد یک شیء WalletTopUpRequest
     * @param id شناسه درخواست
     * @param username نام کاربری
     * @param amount مبلغ درخواستی
     * @param status وضعیت اولیه درخواست
     * @param adminMessage پیام اولیه ادمین (معمولاً خالی)
     */
    public WalletTopUpRequest(int id, String username, double amount, String status, String adminMessage) {
        this.id = id;
        this.username = username;
        this.amount = amount;
        this.status = status;
        this.adminMessage = adminMessage;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getAdminMessage() { return adminMessage; }
}
