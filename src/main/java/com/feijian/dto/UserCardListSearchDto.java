package com.feijian.dto;

public class UserCardListSearchDto {

    private String phone;

    private String cardNumber;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public UserCardListSearchDto(String phone, String cardNumber){
        this.cardNumber = cardNumber;
        this.phone = phone;
    }
}
