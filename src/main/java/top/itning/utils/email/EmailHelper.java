package top.itning.utils.email;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

/**
 * @author itning
 * @date 2019/6/25 15:58
 */
public class EmailHelper {
    private static final String CHECK_EMAIL_STR = "@";
    private final Configuration configuration;
    private final Builder builder;

    private EmailHelper(Builder builder) throws IOException {
        this.builder = builder;
        this.configuration = new Configuration(Configuration.VERSION_2_3_28);
        this.configuration.setDirectoryForTemplateLoading(new File(builder.templateDir));
        this.configuration.setDefaultEncoding("UTF-8");
    }

    public static class Builder {
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
        private SMTPServer smtpServer;

        /**
         * 设置发送者邮箱（必须设置）
         *
         * @param emailSenderUser 发送者邮箱
         * @return this
         */
        public Builder setEmailSenderUser(String emailSenderUser) {
            this.emailSenderUser = emailSenderUser;
            return this;
        }

        /**
         * 设置发送者邮箱密码（必须设置）
         *
         * @param emailSenderPwd 发送者邮箱密码
         * @return this
         */
        public Builder setEmailSenderPwd(String emailSenderPwd) {
            this.emailSenderPwd = emailSenderPwd;
            return this;
        }

        /**
         * 设置模板目录（必须设置）
         *
         * @param templateDir 模板目录
         * @return this
         */
        public Builder setTemplateDir(String templateDir) {
            this.templateDir = templateDir;
            return this;
        }

        /**
         * 设置抄送者，不设置则没有抄送者
         *
         * @param carbonCopyAddress 抄送者邮箱
         * @return this
         */
        public Builder setCarbonCopy(String carbonCopyAddress) {
            this.carbonCopyAddress = carbonCopyAddress;
            return this;
        }

        /**
         * 设置SMTP服务器地址
         *
         * @param smtpServer SMTP服务器地址
         * @return this
         */
        public Builder setSmtpServer(SMTPServer smtpServer) {
            this.smtpServer = smtpServer;
            return this;
        }

        /**
         * 构建
         *
         * @return EmailHelper
         * @throws IOException IOException
         */
        public EmailHelper build() throws IOException {
            return new EmailHelper(this);
        }
    }

    /**
     * SMTP服务器地址
     */
    public enum SMTPServer {
        /**
         * QQ发送邮件的服务器
         */
        QQ("smtp.qq.com"),
        /**
         * 阿里云邮箱发送邮件的服务器
         */
        ALIYUN("smtp.mxhichina.com"),
        /**
         * 网易邮箱发送邮件的服务器
         */
        NTES("smtp.163.com"),
        /**
         * google邮箱发送邮件的服务器
         */
        GMAIL("smtp.gmail.com"),
        /**
         * 新浪邮箱发送邮件的服务器
         */
        SINA("smtp.sina.com");
        private String server;

        SMTPServer(String server) {
            this.server = server;
        }
    }

    /**
     * 1、创建连接对象
     * 设置邮件发送的协议
     * 设置发送邮件的服务器
     * 填写自己的密钥
     * 2、创建邮件对象
     * 设置发件人
     * 设置收件人
     * 设置抄送者
     * 设置邮件主题
     * 设置邮件内容
     * 3、发送邮件
     *
     * @param to      邮件接收者邮箱
     * @param subject 主题
     * @param content 内容
     */
    public void sendEmail(String to, String subject, String content) throws MessagingException {
        if (!to.contains(CHECK_EMAIL_STR)) {
            throw new IllegalArgumentException("接收者邮箱非法,请检查!");
        }
        Message message = getMessage();
        //2.1设置发件人
        message.setFrom(new InternetAddress(builder.emailSenderUser));
        //2.2设置收件人
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
        //2.3设置抄送者（PS:没有这一条网易会认为这是一条垃圾短信，而发不出去）
        if (builder.carbonCopyAddress != null) {
            message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(builder.carbonCopyAddress));
        }
        //2.4设置邮件的主题
        message.setSubject(subject);
        //2.5设置邮件的内容
        message.setContent("" + content + "", "text/html;charset=utf-8");
        // 3、发送邮件
        Transport.send(message);
    }

    /**
     * 发送邮件带附件
     *
     * @param to      邮件接收者邮箱
     * @param subject 主题
     * @param content 内容
     */
    public void sendEmail(String to, String subject, String content, File annexFile) throws MessagingException, UnsupportedEncodingException {
        if (!to.contains(CHECK_EMAIL_STR)) {
            throw new IllegalArgumentException("接收者邮箱非法,请检查!");
        }
        Message message = getMessage();
        // 发件人
        InternetAddress from = new InternetAddress(builder.emailSenderUser);
        message.setFrom(from);
        // 收件人
        InternetAddress re = new InternetAddress(to);
        message.setRecipient(Message.RecipientType.TO, re);
        if (builder.carbonCopyAddress != null) {
            message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(builder.carbonCopyAddress));
        }
        // 邮件主题
        message.setSubject(subject);
        Multipart multipart = new MimeMultipart();
        //添加邮件正文
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(content, "text/html;charset=UTF-8");
        multipart.addBodyPart(contentPart);
        //添加邮件附件
        BodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(annexFile);
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        //MimeUtility.encodeWord可以避免文件名乱码
        attachmentBodyPart.setFileName(MimeUtility.encodeWord(annexFile.getName()));
        multipart.addBodyPart(attachmentBodyPart);
        message.setContent(multipart);
        Transport.send(message);
    }

    /**
     * 发送HTML模板邮件
     *
     * @param templateName 模板名
     * @param dataMap      数据
     * @param to           邮件接收者邮箱
     * @param subject      主题
     * @throws TemplateException  if an exception occurs during template processing
     * @throws IOException        if an I/O exception occurs during writing to the writer.
     * @throws MessagingException 邮件发送异常
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> dataMap) throws IOException, TemplateException, MessagingException {
        //获取模板
        Template template = configuration.getTemplate(templateName);
        StringWriter stringWriter = new StringWriter();
        //生成HTML
        template.process(dataMap, stringWriter);
        stringWriter.flush();
        sendEmail(to, subject, stringWriter.toString());
    }

    /**
     * 发送HTML模板邮件带附件
     *
     * @param templateName 模板名
     * @param dataMap      数据
     * @param to           邮件接收者邮箱
     * @param subject      主题
     * @throws TemplateException  if an exception occurs during template processing
     * @throws IOException        if an I/O exception occurs during writing to the writer.
     * @throws MessagingException 邮件发送异常
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> dataMap, File annexFile) throws IOException, TemplateException, MessagingException {
        //获取模板
        Template template = configuration.getTemplate(templateName);
        StringWriter stringWriter = new StringWriter();
        //生成HTML
        template.process(dataMap, stringWriter);
        stringWriter.flush();
        sendEmail(to, subject, stringWriter.toString(), annexFile);
    }

    private Message getMessage() {
        //1、创建连接对象
        Properties props = new Properties();
        //1.1设置邮件发送的协议
        props.put("mail.transport.protocol", "smtp");
        //1.2设置发送邮件的服务器
        props.put("mail.smtp.host", builder.smtpServer.server);
        //1.3需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
        props.put("mail.smtp.auth", "true");
        //1.4下面一串是发送邮件用465端口，如果不写就是以25端口发送，阿里云已经关闭了25端口
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        //1.5认证信息
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(builder.emailSenderUser, builder.emailSenderPwd);
            }
        });
        //2、创建邮件对象
        return new MimeMessage(session);
    }
}