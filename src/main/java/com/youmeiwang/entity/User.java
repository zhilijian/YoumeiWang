package com.youmeiwang.entity;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "youmei_user")
public class User {

	//用户ID userID
	@Indexed
	private String userID;
	//用户姓名 username
	private String username;
	//用户昵称 nickname
	private String nickname;
	//手机号 phone
	private String phone;
	//用户头像(文件路径) portrait
	private String portrait;
	//支付宝 Alipay
	private String Alipay;
	//QQ账号 QQ
	private String QQ;
	//邮箱 email
	private String email;
	//游币数量 youbiAmount
	private Long youbiAmount;
	//余额 balance
	private Long balance;
	//VIP类别 VIPKind
	//0：大众用户	 0：个人VIP 1：企业VIP 2：原创VIP
	private Integer[] VIPKind = {0};
	//会员种类 memberKind 0：普通用户 1：原创用户
	private Integer memberKind = 0;
	//会员到期时间 memberExpirationTime
	private String memberExpirationTime;
	//待审核作品
	private List<String> verifyingWork;
	//已通过作品
	private List<String> verifiedWork;
	//未通过作品
	private List<String> notPassWork;
	//收藏的作品
	private List<String> collectWork;
	//下载的作品
	private List<String> downWork;
	//申请原创作者 ApplyForOriginal
	//0：未申请 1：申请中 2：申请成功 3：申请驳回
	private Integer ApplyForOriginal = 0;
	//审核信息
	private String verifyMessage;

	public User() {
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getAlipay() {
		return Alipay;
	}

	public void setAlipay(String alipay) {
		Alipay = alipay;
	}

	public String getQQ() {
		return QQ;
	}

	public void setQQ(String qQ) {
		QQ = qQ;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getYoubiAmount() {
		return youbiAmount;
	}

	public void setYoubiAmount(Long youbiAmount) {
		this.youbiAmount = youbiAmount;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public Integer[] getVIPKind() {
		return VIPKind;
	}

	public void setVIPKind(Integer[] vIPKind) {
		VIPKind = vIPKind;
	}

	public Integer getMemberKind() {
		return memberKind;
	}

	public void setMemberKind(Integer memberKind) {
		this.memberKind = memberKind;
	}

	public String getMemberExpirationTime() {
		return memberExpirationTime;
	}

	public void setMemberExpirationTime(String memberExpirationTime) {
		this.memberExpirationTime = memberExpirationTime;
	}

	public List<String> getVerifyingWork() {
		return verifyingWork;
	}

	public void setVerifyingWork(List<String> verifyingWork) {
		this.verifyingWork = verifyingWork;
	}

	public List<String> getVerifiedWork() {
		return verifiedWork;
	}

	public void setVerifiedWork(List<String> verifiedWork) {
		this.verifiedWork = verifiedWork;
	}

	public List<String> getNotPassWork() {
		return notPassWork;
	}

	public void setNotPassWork(List<String> notPassWork) {
		this.notPassWork = notPassWork;
	}

	public List<String> getCollectWork() {
		return collectWork;
	}

	public void setCollectWork(List<String> collectWork) {
		this.collectWork = collectWork;
	}

	public List<String> getDownWork() {
		return downWork;
	}

	public void setDownWork(List<String> downWork) {
		this.downWork = downWork;
	}

	public Integer getApplyForOriginal() {
		return ApplyForOriginal;
	}

	public void setApplyForOriginal(Integer applyForOriginal) {
		ApplyForOriginal = applyForOriginal;
	}

	public String getVerifyMessage() {
		return verifyMessage;
	}

	public void setVerifyMessage(String verifyMessage) {
		this.verifyMessage = verifyMessage;
	}

}
