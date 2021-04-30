package com.kakao.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Pattern;

public class testMain {
	
	static int ansMax=0, map[][], dir[][]={{1, -1}, {1, 0}, {1, 1}};  //과제 2-2번
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		System.out.println("과제2-1 입력 : ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = br.readLine();
		String 	print= "";
		
		while (true) {
			
			print = stringDecode(str);
			if(!print.contains("[")) {
				break;
			}
			str= print;
		}
		System.out.println("result : " + print);
		
		
		System.out.println("과제2-2 입력 : ");
		
		BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
        int x=Integer.parseInt(br1.readLine());
        int y=Integer.parseInt(br1.readLine());
        int[] data =Arrays.stream(br1.readLine().split(",")).mapToInt(Integer::parseInt).toArray();

        int idx=0;
        map = new int[y][x];
        for(int i=0; i<y; i++) for(int j=0; j<x; j++) map[i][j] = data[idx++];

        march(0, 0, 0, x - 1, map[0][0] + map[0][x-1]);

        System.out.println("result : " + ansMax);
		
		
		
	}
	
	static String stringDecode(String str) {
		///과제 2-1
		String arr[] = str.split("");
		String text = "";
		int num= 0;
		
		
		int openCnt=0;
		int closeCnt=0;
		String inner="";
		for (String temp: arr) {
			
			if("[".equals(temp)) {
				if(openCnt != closeCnt) {
					inner+=temp;
				}
				openCnt++;
			} else if("]".equals(temp)) {
				closeCnt++;
				if(openCnt != closeCnt) {
					if(num>0) {
						inner+=temp;
					} else {
						text += temp;
					}
				} else if(num>0 && openCnt == closeCnt) {
					for(int x=0;x<num;x++) {
							text+=inner;
					}
					num=0;
					inner="";
					openCnt--;
					closeCnt--;
				} else {
					text += temp;	
				}
				
			} else if(num ==0 && Pattern.matches("^[0-9]*$", temp)) {
				num= Integer.valueOf(temp);
			} else {
				if(openCnt > 0) {
					inner+=temp;
				} else {
					text += temp;
				}
			}
					
		}
		
		return text;
	}
	
	
	private static void march(int neoR, int neoC, int fr, int fc, int score) {
		if(neoR >= map.length -1) {
			if(ansMax < score)
				ansMax = score;
			return;
		}
		
		for(int d=0; d<3; d++){
			int nnr=neoR + dir[d][0], nnc=neoC + dir[d][1];
			if(nnr<0 || nnc<0 || nnr>=map.length ||nnc>=map[0].length) continue;
			
			
			for(int dd=0; dd<3; dd++){
				int nfr=fr + dir[dd][0], nfc=fc + dir[dd][1];
				if(nfr<0 || nfc<0 || nfr>=map.length ||nfc>=map[0].length) continue;
				
				int addScore = (nnc==nfc) ? map[nnr][nnc] : map[nnr][nnc] + map[nfr][nfc];//같은 칸으로 이동할 수 있지만, 점수는 한명에게만
				
				march(nnr, nnc, nfr, nfc, score + addScore);
			}
		}
	}
	
}
