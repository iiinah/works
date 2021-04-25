package com.kakao.web;

import lombok.Data;

@Data
//@ToString
public class AccountVO {

	public String acntNum;  //계좌번호 
	public String acntNm;  //계좌명
	public String mngCd;   //관리점코드
	
	public int sumAmt2018;
	public int sumAmt2019;
	public String year;
}
