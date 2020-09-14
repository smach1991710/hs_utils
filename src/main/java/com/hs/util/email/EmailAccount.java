package com.hs.util.email;

/**
 * @Author: songhao
 * @Date: 2020/9/14
 * @Description: 邮件账号信息
 */
public class EmailAccount {

    // 邮件账号信息
    public String myEmailAccount = "";
    public String myEmailPassword = "";

    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般格式为: smtp.xxx.com
    // 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
    public String myEmailSMTPHost = "smtp.exmail.qq.com";

    public EmailAccount(String emailAccount,String emailPassword,String emailSMTPHost){
        this.myEmailAccount = emailAccount;
        this.myEmailPassword = emailPassword;
        this.myEmailSMTPHost = emailSMTPHost;
    }

    public String getMyEmailAccount() {
        return myEmailAccount;
    }

    public void setMyEmailAccount(String myEmailAccount) {
        this.myEmailAccount = myEmailAccount;
    }

    public String getMyEmailPassword() {
        return myEmailPassword;
    }

    public void setMyEmailPassword(String myEmailPassword) {
        this.myEmailPassword = myEmailPassword;
    }

    public String getMyEmailSMTPHost() {
        return myEmailSMTPHost;
    }

    public void setMyEmailSMTPHost(String myEmailSMTPHost) {
        this.myEmailSMTPHost = myEmailSMTPHost;
    }
}
