package com.kakao.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;


@Service
public class GetInfoService {
	
	//고객별 각 년도 합계 리스트
	public List<AccountVO> custSum() {
		String path = "src/main/resources/static/data/과제1_데이터_거래내역.csv";
		List<TradeVO> tradeVOList = readCsvToTradeVo(path);
		
		path = "src/main/resources/static/data/과제1_데이터_계좌정보.csv";
		List<AccountVO> accountVOList = readCsvToAccountVo(path);
		
		for(TradeVO tradeVO : tradeVOList) {
			for(AccountVO accountVO :accountVOList) {
				// 각 계좌별 18년,19년 합계 구함
				if("N".equals(tradeVO.getCancelYn())) { //취소 아니면
					if(accountVO.getAcntNum().equals(tradeVO.getAcntNum())) {
						String year = tradeVO.getTradeDate().substring(0, 4);
						int amount = Integer.parseInt(String.valueOf(tradeVO.getAmount())); 
						if("2018".equals(year)) {
							accountVO.setSumAmt2018( accountVO.getSumAmt2018() + amount - tradeVO.getCommission());
						} else {
							accountVO.setSumAmt2019( accountVO.getSumAmt2019() + amount - tradeVO.getCommission());
						}
					}
					
				}
			}
			
		}
		return accountVOList;
	}
	
	///지점별 각 년도 합계 리스트
	public List<MngtVO> mngtSum(List<AccountVO> accountVOList ) {
	
		String path = "src/main/resources/static/data/과제1_데이터_관리점정보.csv";
		List<MngtVO> mngtVOLsit = readCsvToMngtVo(path);
		for(AccountVO accountVO:accountVOList) {
			for(MngtVO mngtVO: mngtVOLsit) {
				if(mngtVO.getMngCd().equals(accountVO.getMngCd())) {
					int sum2018 = mngtVO.getSumAmt2018();
					int sum2019 = mngtVO.getSumAmt2019();
					sum2018 = sum2018 + Integer.parseInt(String.valueOf(accountVO.getSumAmt2018())); 
					sum2019 = sum2019 + Integer.parseInt(String.valueOf(accountVO.getSumAmt2019())); 
					
					mngtVO.setSumAmt2018(sum2018);
					mngtVO.setSumAmt2019(sum2019);
					
				}
			}
		}
		
		return mngtVOLsit;
	
	}
		
	public List<TradeVO> readCsvToTradeVo(String filename){
		
		List<TradeVO> data = new ArrayList<TradeVO>();
		try {
			//csv 파일 읽기
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"),',','"',0);
			
			List<String[]> temp = new ArrayList<String[]>();
			temp = reader.readAll();
			
			temp.remove(0);
			for(String[] tempvo:temp) {
				TradeVO tradeVO = new TradeVO();
				tradeVO.setTradeDate(tempvo[0]);
				tradeVO.setAcntNum(tempvo[1]);
				tradeVO.setTradeNum(tempvo[2]);
				tradeVO.setAmount(tempvo[3]);
				tradeVO.setCommission(Integer.parseInt(tempvo[4]));
				tradeVO.setCancelYn(tempvo[5]);
				data.add(tradeVO);
			}
			
			reader.close();
			
			
		}catch(Exception e) {
			e.getStackTrace();
		}
		
		return data;
	}
	
	
	public List<MngtVO> readCsvToMngtVo(String filename){
		
		List<MngtVO> data = new ArrayList<MngtVO>();
		try {
			//csv 파일 읽기
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"),',','"',0);
			
			List<String[]> temp = new ArrayList<String[]>();
			temp = reader.readAll();
			
			temp.remove(0);
			for(String[] tempvo:temp) {
				MngtVO mngtVO = new MngtVO();
				mngtVO.setMngCd(tempvo[0]);
				mngtVO.setMngNm(tempvo[1]);
				data.add(mngtVO);
			}
			
			reader.close();
			
			
		}catch(Exception e) {
			e.getStackTrace();
		}
		
		return data;
	}
	
	
	public List<AccountVO> readCsvToAccountVo(String filename){
		
		List<AccountVO> data = new ArrayList<AccountVO>();
		try {
			//csv 파일 읽기
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"),',','"',0);
			
			List<String[]> temp = new ArrayList<String[]>();
			temp = reader.readAll();
			
			temp.remove(0);
			for(String[] tempvo:temp) { 
				AccountVO accountVO = new AccountVO();
				accountVO.setAcntNum(tempvo[0]);
				accountVO.setAcntNm(tempvo[1]);
				accountVO.setMngCd(tempvo[2]);
				data.add(accountVO);
			}
			
			reader.close();
			
		}catch(Exception e) {
			e.getStackTrace();
		}
		
		return data;
	}
	
}
				
