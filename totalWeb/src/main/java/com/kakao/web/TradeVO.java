package com.kakao.web;

import lombok.Data;

@Data
//@ToString
public class TradeVO {

	public String tradeDate;  //거래일자
	public String acntNum;  //계좌번호 
	public String tradeNum; //거래번호
	public String amount;  //금액
	public int commission;//수수료
	public String cancelYn ;//취소여부


}
