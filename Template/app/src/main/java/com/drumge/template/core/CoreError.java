/**
 * 
 */
package com.drumge.template.core;

/**
 * @author daixiang
 *
 */
public class CoreError {
	
	// error code 定义
	public static final int Network_Error = 1000;
	public static final int Db_Error = 1001;
	public static final int Timeout_Error = 1002;
	public static final int Server_Error = 1003;
	public static final int Unknown_Error = 1004;
	
	// auth模块错误码
	public static final int Auth_UserNotExist = 2000;
	public static final int Auth_PasswordError = 2001;
	public static final int Auth_UserBanned = 2002;
	public static final int AUTHE_FAIL = 2003;
	public static final int AUTHRES_LOGIN_DATA_ERR = 2004;
	public static final int RECEIVE_UDB_RES = 2005;
	public static final int UDB_REJECT_ANONYM = 2006;

	// auth模块登录UDB返回错误码
	public static final int UDB_UNKNOW_ERR = 2100;
	public static final int UDB_TICKET_ERR = 2101;
	public static final int UDB_LOCKED = 2102;
	public static final int UDB_MOB_TOKEN_ERR = 2103;
	public static final int UDB_HW_TOKEN_ERR = 2104;
	public static final int UDB_SEC_QUESTION_ERR = 2105;
	public static final int UDB_SMS_CODE_ERR = 2106;
	public static final int UDB_YID_LOGIN_LIMIT = 2107;
	public static final int LSER_NO_WEB_LOGIN_AUTH = 2108;
	public static final int UDB_ACCTINFO_ERR = 2109;
	public static final int LRES_OTP_ERR = 2110;
	public static final int UDB_RETRY = 2111;
	public static final int UDB_PARAM_ERR = 2112;
	public static final int NEED_NEXT_VERIFY = 2113;
	public static final int UDB_FROZEN = 2114;
	public static final int UDB_REJECT = 2115;
	public static final int UDB_REJECT_ANONYM_REQUEST_TOO_MUCH = 2116;
	public static final int UDB_REJECT_ANONYM_LOGIN_TOO_MUCH = 2117;
	public static final int UDB_REJECT_ANONYM_BAN_IP = 2118;
	public static final int LRES_MOB_TOKEN_ERR = 2119;//手机令牌验证错误
	public static final int LRES_HW_TOKEN_ERR = 2120;//硬件令牌验证错误
	public static final int LRES_SEC_QUESTION_ERR = 2121;//密保问题验证错误
	public static final int LRES_SMS_CODE_ERR = 2122;//短信验证错误
	public static final int UDB_BAN_ERR = 2123;//封禁

	// channel模块错误码
	//(进入频道:joinChannel 错误码)
	public static final int Channel_Join_NoChannel = 3000;				//没有这个频道号
	public static final int Channel_Join_Exist = 3001;					//已经在这个频道

	// im模块错误码
	public static final int Im_PasswordError = 4000;
	public static final int Im_UserBanned = 4001;
	public static final int Im_UserNotExist = 4002;
    public static final int Im_NotMyGroupOrFolder = 4003;



	public enum Domain {
		Db,
		Auth,
		User,
		Im,
		Channel,
		Media
	}
	
	public Domain domain;
	public int code;
	public String message;
	public Throwable throwable;

	/**
	 * 
	 */
	public CoreError(Domain domain, int code, String message) {
		this.domain = domain;
		this.message = message;
		this.code = code;
	}

	public CoreError(Domain domain, int code) {
		this.domain = domain;
		this.code = code;
	}

	public CoreError(Domain domain, int code, String message,
			Throwable throwable) {
		this.domain = domain;
		this.code = code;
		this.message = message;
		this.throwable = throwable;
	}

}
