package com.kakao.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

 
@Controller
@RequestMapping("kakao")
public class GetInfoController {

	private GetInfoService getInfoService;
	
	public GetInfoController() {}
	
	@Autowired
	public GetInfoController(GetInfoService getInfoService) {
		this.getInfoService = getInfoService;
		
	}
	
	
	//1번 2018년, 2019년 각 연도별 합계 금액이 가장 많은 고객을 추출하는 API 개발.(단, 취소여부가 ‘Y’ 거래는 취소된 거래임, 합계 금액은 거래금액에서 수수료를 차감한 금액임)
	//http://localhost:8080/kakao/cust/sumMost
	@ResponseBody
	@RequestMapping("/cust/sumMost")
	public String sumMostCust (HttpServletRequest request, HttpServletResponse response) {
		List<AccountVO> accountVOList = getInfoService.custSum();
		
		
		List<AccountVO> resultList =  new ArrayList<AccountVO>();
		AccountVO resultVO = new AccountVO();
		resultVO = accountVOList.stream()
				.max((c1, c2) -> Integer.compare(c1.getSumAmt2018(), c2.getSumAmt2018()))
				.get();
		resultVO.setYear("2018");
		resultList.add(resultVO);
		
		resultVO = new AccountVO();
		resultVO = accountVOList.stream()
				.max((c1, c2) -> Integer.compare(c1.getSumAmt2019(), c2.getSumAmt2019()))
				.get();
		resultVO.setYear("2019");
		resultList.add(resultVO);
		
		JSONArray parameterList = new JSONArray();
		for(AccountVO resultvo : resultList) {
			JSONObject parameter = new JSONObject();
			parameter.put("year", resultvo.getYear());
			parameter.put("name", resultvo.getAcntNm());
			parameter.put("acctNo", resultvo.getAcntNum());
			if("2018".equals(resultvo.getYear())) {
				parameter.put("sumAmt", resultvo.getSumAmt2018());
			} else {
				parameter.put("sumAmt", resultvo.getSumAmt2019());
			}
			parameterList.put(parameter);
		}
		
		return parameterList.toString();
	}
	
	
	
	
	
	
	//2번 2018년 또는 2019년에 거래가 없는 고객을 추출하는 API 개발.
	//http://localhost:8080/kakao/cust/noTrade
	@ResponseBody
	@RequestMapping("/cust/noTrade")
	public String noTrade (HttpServletRequest request, HttpServletResponse response) {
		
		List<AccountVO> accountVOList = getInfoService.custSum();
		
		JSONArray parameterList = new JSONArray();
		for(AccountVO account : accountVOList){
			if (account.getSumAmt2018()==0 || account.getSumAmt2019()==0) {
				String year ="";
				year = (account.getSumAmt2018()==0 && account.getSumAmt2019()==0 ? "2018, 2019" :
					account.getSumAmt2018()==0 ? "2018" : "2019" );
				
				JSONObject parameter = new JSONObject();
				parameter.put("year", year);
				parameter.put("name", account.getAcntNm());
				parameter.put("acctNo", account.getAcntNum());
				
				parameterList.put(parameter);
			}
			
		}
		return parameterList.toString();
	}

	//3번 연도별 관리점별 거래금액 합계를 구하고 합계금액이 큰 순서로 출력하는 API 개발.( 취소여부가 ‘Y’ 거래는 취소된 거래임)
	//http://localhost:8080/kakao/cust/mngtSum
	@ResponseBody
	@RequestMapping("/cust/mngtSum")
	public String mngtSumList (HttpServletRequest request, HttpServletResponse response) {
				
		List<AccountVO> accountVOList = getInfoService.custSum();
		List<MngtVO> mngtVOLsit = getInfoService.mngtSum(accountVOList);
		
		List<MngtVO> sum2018List = mngtVOLsit.stream().sorted(Comparator.comparing(MngtVO::getSumAmt2018).reversed()).collect(Collectors.toList());
		List<MngtVO>  sum2019List = mngtVOLsit.stream().sorted(Comparator.comparing(MngtVO::getSumAmt2019).reversed()).collect(Collectors.toList());
		
		JSONArray parameterList = new JSONArray();
		JSONObject parameter = new JSONObject();
		
		JSONArray subParameterList = new JSONArray();
		for(MngtVO mngVO : sum2018List){
			JSONObject subParameter = new JSONObject();
			subParameter.put("brName", mngVO.getMngNm());
			subParameter.put("brCode", mngVO.getMngNm());
			subParameter.put("sumAmt", mngVO.getSumAmt2018());
			subParameterList.put(subParameter);
		}
		parameter.put("year","2018");
		parameter.put("dataList", subParameterList);
		parameterList.put(parameter);
		
		parameter = new JSONObject();
		subParameterList = new JSONArray();
		for(MngtVO mngVO : sum2019List){
			JSONObject subParameter = new JSONObject();
			subParameter.put("brName", mngVO.getMngNm());
			subParameter.put("brCode", mngVO.getMngNm());
			subParameter.put("sumAmt", mngVO.getSumAmt2018());
			subParameterList.put(subParameter);
		}
		parameter.put("year","2019");
		parameter.put("dataList", subParameterList);
		parameterList.put(parameter);
		
		return parameterList.toString();
	
	
	}
	
	
	
	//4번 분당점과 판교점을 통폐합하여 판교점으로 관리점 이관을 하였습니다. 지점명을 입력하면 해당지점의 거래금액 합계를 출력하는 API 개발
	//http://localhost:8080/kakao/cust/mngtSum/saerch?brName=판교점
	@ResponseBody
	@RequestMapping("/cust/mngtSum/saerch")
	public String searchmngtSumList (HttpServletRequest request, HttpServletResponse response) {
		JSONArray parameterList = new JSONArray();
		JSONObject parameter = new JSONObject();
		
		String brName = request.getParameter("brName");
		
		if(!brName.isEmpty() && !"분당점".equals(brName)){
			List<AccountVO> accountVOList = getInfoService.custSum();
			List<MngtVO> mngtVOLsit = getInfoService.mngtSum(accountVOList);
	
			for(MngtVO mngtVO :mngtVOLsit) {
				if("A".equals(mngtVO.getMngCd())) {  //이관
					System.out.println("기존 판교점" + mngtVO.toString());				
					int sum2018 = 0;
					int sum2019 = 0;
					for(MngtVO tempVO :mngtVOLsit) {
						if("B".equals(tempVO.getMngCd())|| "분당점".equals(tempVO.getMngNm())) {  
							System.out.println("분당점" + tempVO.toString());		
							sum2018 = tempVO.getSumAmt2018();
							sum2019 = tempVO.getSumAmt2019(); 
							break;
						}
					}
					mngtVO.setSumAmt2018(mngtVO.getSumAmt2018() + sum2018 );
					mngtVO.setSumAmt2019(mngtVO.getSumAmt2019() + sum2019 );
				System.out.println("분당점 통합한 판교점" + mngtVO.toString());		
				} 
				
				if(brName.equals(mngtVO.getMngNm())) {
					parameter.put("brName",mngtVO.getMngNm());
					parameter.put("brCode",mngtVO.getMngCd());
					parameter.put("sumAmt", mngtVO.getSumAmt2018() + mngtVO.getSumAmt2019());
				}
				
			}
			
			
		} else {
			parameter.put("code","404");
			parameter.put("메세지", "br code not found error");
		}
	
		return parameter.toString();
	}	
	

}
