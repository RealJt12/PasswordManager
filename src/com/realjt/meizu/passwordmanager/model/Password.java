package com.realjt.meizu.passwordmanager.model;

import java.util.Date;

import com.realjt.meizu.passwordmanager.utils.DateUtils;
import com.realjt.meizu.passwordmanager.utils.EncryptUtils;

/**
 * Password描述类
 * 
 * @author Administrator
 */
public class Password
{
	private int id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 账号
	 */
	private String account;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 最后修改时间
	 */
	private Date reviseTime;

	/**
	 * 分类
	 */
	private Classification classification;

	public Password()
	{
		Date date = new Date();
		this.createTime = date;
		this.reviseTime = date;
		this.classification = Classification.initClassification(1);
	}

	public Password(String name, String account, String password,
			String remarks, Classification classification)
	{
		this();
		this.name = name;
		this.account = account;
		this.password = password;
		this.remarks = remarks;
		this.classification = classification;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		if (account == null)
			this.account = "";
		else
			this.account = account;
	}

	public String getPassword()
	{
		if (password == null)
			return "";
		return password;
	}

	public void setPassword(String password)
	{
		if (password == null)
			this.password = "";
		else
			this.password = password;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		if (remarks == null)
			this.remarks = "";
		else
			this.remarks = remarks;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public void setCreateTime(String dateStr)
	{
		this.createTime = DateUtils.stringToDate(dateStr);
	}

	public Date getReviseTime()
	{
		return reviseTime;
	}

	public void setReviseTime(Date reviseTime)
	{
		this.reviseTime = reviseTime;
	}

	public void setReviseTime(String dateStr)
	{
		this.reviseTime = DateUtils.stringToDate(dateStr);
	}

	public Classification getClassification()
	{
		return classification;
	}

	public void setClassification(Classification classification)
	{
		this.classification = classification;
	}

	public void setClassification(String classification)
	{
		this.classification = Classification.initClassification(Integer
				.valueOf(classification));
	}

	@Override
	public boolean equals(Object object)
	{
		if (this == object)
		{
			return true;
		}
		if (object instanceof Password)
		{
			Password password = (Password) object;
			if (password.getName().equals(this.name)
					&& password.getAccount().equals(this.account)
					&& password.getPassword().equals(this.password)
					&& password.getRemarks().equals(this.remarks)
					&& password.getClassification() == this.classification)
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return createTime.hashCode() + reviseTime.hashCode();
	}

	public String toJson()
	{
		String jsonStr = "";
		jsonStr += "{\"name\":\"" + name + "\",\"account\":\"" + account
				+ "\",\"password\":\"" + password + "\",\"remarks\":\""
				+ remarks + "\",\"createtime\":\""
				+ DateUtils.dateToString(createTime) + "\",\"revisetime\":\""
				+ DateUtils.dateToString(reviseTime)
				+ "\",\"classification\":\""
				+ classification.getClassification() + "\"}";
		return jsonStr;
	}

	public String toEncryptJson(String password)
	{
		String jsonStr = "";
		jsonStr += "{\"name\":\""
				+ EncryptUtils.encrypt(name, password)
				+ "\",\"account\":\""
				+ EncryptUtils.encrypt(account, password)
				+ "\",\"password\":\""
				+ EncryptUtils.encrypt(this.password, password)
				+ "\",\"remarks\":\""
				+ EncryptUtils.encrypt(remarks, password)
				+ "\",\"createtime\":\""
				+ EncryptUtils.encrypt(DateUtils.dateToString(createTime),
						password)
				+ "\",\"revisetime\":\""
				+ EncryptUtils.encrypt(DateUtils.dateToString(reviseTime),
						password)
				+ "\",\"classification\":\""
				+ EncryptUtils.encrypt(
						String.valueOf(classification.getClassification()),
						password) + "\"}";
		return jsonStr;
	}

}
