package project5;


public class QueryRequest {
    private String trackingNumber;   // 对应 JSON 里的单号
    private String phone;            // 对应 JSON 里的手机号

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
