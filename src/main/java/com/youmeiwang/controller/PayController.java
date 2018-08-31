package com.youmeiwang.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.config.CommonConfig;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.PayService;
import com.youmeiwang.service.PurchaseService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.util.SetUtil;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/pay")
public class PayController {

	@Autowired
	private UserService userService;
	
	@Autowired 
	private WorkService workService;
	
	@Autowired
	private PurchaseService purchaseService;
	
	@Autowired
	private PayService payService;
	
	@GetMapping("/purchasework")
	public SimpleVO purchaseWork(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workID", required=true) String workID,
			HttpSession session ) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "请先确认登录后再操作。"); 
//		}

		Work work = workService.queryWork("workID", workID);
		if (work == null || work.getIsDelete() == true) {
			return new SimpleVO(false, "该作品不存在或已被删除。"); 
		}
		
		try {
			User user1 = userService.queryUser("userID", userID);
			User user2 = userService.queryUser("username", work.getAuthor());
			if (user2 == null) {
				return new SimpleVO(false, "模型作者不存在或已销户。"); 
			}
			if (work.getPrimaryClassification() == 1) {
				Double balance1 = user1.getBalance() - work.getPrice();
				Double balance2 = 0.0;
				if (user1.getVipKind().contains(2)) {
					balance2 = user2.getBalance() + work.getPrice() * CommonConfig.commissionRate;
				} else {
					balance2 = user2.getBalance() + work.getPrice();
				}
				if (balance1 < 0) {
					return new SimpleVO(false, "余额不足，请先充值。");
				}
				userService.setUser("userID", userID, "balance", balance1);
				userService.setUser("username", work.getAuthor(), "balance", balance2);
				purchaseService.addPurchase(userID, 2, work.getWorkID(), (double)work.getPrice(), (double)work.getPrice(), null, null);
				payService.createTransaction(userID, workID, null, 0, 1);
				payService.createTransaction(user2.getUserID(), null, work.getPrice() * CommonConfig.commissionRate, 0, 0);
			} else {
				Long youbiAmount1 = user1.getYoubiAmount() - work.getPrice();
				Long youbiAmount2 = user2.getYoubiAmount() + work.getPrice();
				if (youbiAmount1 < 0) {
					return new SimpleVO(false, "游币不足，请先兑换。");
				}
				userService.setUser("userID", userID, "youbiAmount", youbiAmount1);
				userService.setUser("username", work.getAuthor(), "youbiAmount", youbiAmount2);
				purchaseService.addPurchase(userID, 3, work.getWorkName(), null, null, work.getPrice(), work.getPrice());
				payService.createTransaction(userID, workID, null, 1, 1);
				payService.createTransaction(user2.getUserID(), null, (double)work.getPrice(), 1, 0);
			}
			List<String> purchaseWork1 = new ArrayList<String>();
			if (user1.getPurchaseWork() != null) {
				purchaseWork1 = user1.getPurchaseWork();
			}
			List<String> purchaseWork2 = ListUtil.addElement(purchaseWork1, workID);
			userService.setUser("userID", userID, "purchaseWork", purchaseWork2);
			return new SimpleVO(true, "购买作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/purchasevip")
	public SimpleVO purchaseVIP(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="vipKind", required=true) Integer vipKind,
			@RequestParam(name="fee", required=true) Integer fee,
			@RequestParam(name="monthNum", required=true) Integer monthNum,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "请先确认登录后再操作。"); 
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			Double balance = user.getBalance() - fee;
			if (balance < 0) {
				return new SimpleVO(false, "余额不足，请先充值。");
			}
			userService.setUser("userID", userID, "balance", balance);
			
			Calendar calendar = Calendar.getInstance();
			switch (vipKind) {
			case 1:
				userService.setUser("userID", userID, "vipKind", SetUtil.addElement(user.getVipKind(), 1));
				
				Long shareVIPTime = 0l;
				if (user.getShareVIPTime() == null || user.getShareVIPTime() < System.currentTimeMillis()) {
					shareVIPTime = System.currentTimeMillis();
				} else {
					shareVIPTime = user.getShareVIPTime();
				}
				
				calendar.setTime(new Date(shareVIPTime));
				calendar.add(Calendar.MONDAY, monthNum);
				userService.setUser("userID", userID, "shareVIPTime", calendar.getTimeInMillis());
				purchaseService.addPurchase(userID, 1, "共享VIP", (double)fee, (double)fee, null, null);
				break;
			
			case 2:
				userService.setUser("userID", userID, "vipKind", SetUtil.addElement(user.getVipKind(), 2));
				
				Long originalVIPTime = 0l;
				if (user.getOriginalVIPTime() == null || user.getOriginalVIPTime() < System.currentTimeMillis()) {
					originalVIPTime = System.currentTimeMillis();
				} else {
					originalVIPTime = user.getOriginalVIPTime();
				}
				
				calendar.setTime(new Date(originalVIPTime));
				calendar.add(Calendar.MONDAY, monthNum);
				userService.setUser("userID", userID, "originalVIPTime", calendar.getTimeInMillis());
				purchaseService.addPurchase(userID, 1, "原创VIP", (double)fee, (double)fee, null, null);
				break;
			
			case 3:
				userService.setUser("userID", userID, "vipKind", SetUtil.addElement(user.getVipKind(), 3));
				
				Long companyVIPTime = 0l;
				if (user.getCompanyVIPTime() == null || user.getCompanyVIPTime() < System.currentTimeMillis()) {
					companyVIPTime = System.currentTimeMillis();
				} else {
					companyVIPTime = user.getCompanyVIPTime();
				}
				
				calendar.setTime(new Date(companyVIPTime));
				calendar.add(Calendar.MONDAY, monthNum);
				userService.setUser("userID", userID, "companyVIPTime", calendar.getTimeInMillis());
				purchaseService.addPurchase(userID, 1, "企业VIP", (double)fee, (double)fee, null, null);
				break;

			default:
				break;
			}
			payService.createTransaction(userID, null, (double)fee, 0, 5);
			return new SimpleVO(true, "购买VIP成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		} 
	}
	
	@GetMapping("/exchange")
	public SimpleVO exchange(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="money", required=true) Integer money,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "请先确认登录后再操作。"); 
//		}
		
		try {
			payService.createTransaction(userID, null, (double)money, 0, 3);
			User user = userService.queryUser("userID", userID);
			Double balance = user.getBalance() - money;
			Long youbiAmount = user.getYoubiAmount() + money * 10;
			if (balance < 0) {
				return new SimpleVO(false, "余额不足，请先充值。"); 
			}
			userService.setUser("userID", userID, "balance", balance);
			userService.setUser("userID", userID, "youbiAmount", youbiAmount);
			return new SimpleVO(true, "余额兑换游币成功。"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
	
	
	
}
