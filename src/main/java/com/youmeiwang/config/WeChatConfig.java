package com.youmeiwang.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WeChatConfig {

	public static String APPID = "wx4f1788d8ac74e1e3";
	public static String MCH_ID = "1433133902";
	public static String KEY = "E32D3D18EA874A3E8E1A711D140BD975";
	public static String NOTIFY_URL = "http://www.linshaocong.cn:8081/wechatpay/wechatnotify";
	public static String SIGN_TYPE = "MD5";
	public static String CHARSET = "utf-8";
	public static String FEE_TYPE = "CNY";
	public static String TRADE_TYPE = "NATIVE";
	
	
}