package top.itning.utils.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Email 配置
 *
 * @author itning
 */
@ConfigurationProperties(prefix = "util.email")
public class EmailProperties {
    /**
     * 启用
     */
    private boolean enabled = true;
    /**
     * 邮件发送者账户（必须设置）
     */
    private String emailSenderUser;
    /**
     * 邮件发送者密码（必须设置）
     */
    private String emailSenderPwd;
    /**
     * HTML模板目录（必须设置）
     */
    private String templateDir;
    /**
     * 抄送者
     */
    private String carbonCopyAddress;
    /**
     * SMTP服务器地址
     */
    private EmailHelper.SMTPServer smtpServer;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmailSenderUser() {
        return emailSenderUser;
    }

    public void setEmailSenderUser(String emailSenderUser) {
        this.emailSenderUser = emailSenderUser;
    }

    public String getEmailSenderPwd() {
        return emailSenderPwd;
    }

    public void setEmailSenderPwd(String emailSenderPwd) {
        this.emailSenderPwd = emailSenderPwd;
    }

    public String getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public String getCarbonCopyAddress() {
        return carbonCopyAddress;
    }

    public void setCarbonCopyAddress(String carbonCopyAddress) {
        this.carbonCopyAddress = carbonCopyAddress;
    }

    public EmailHelper.SMTPServer getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(EmailHelper.SMTPServer smtpServer) {
        this.smtpServer = smtpServer;
    }

    @Override
    public String toString() {
        return "EmailProperties{" +
                "enabled=" + enabled +
                ", emailSenderUser='" + emailSenderUser + '\'' +
                ", emailSenderPwd='" + emailSenderPwd + '\'' +
                ", templateDir='" + templateDir + '\'' +
                ", carbonCopyAddress='" + carbonCopyAddress + '\'' +
                ", smtpServer=" + smtpServer +
                '}';
    }
}
