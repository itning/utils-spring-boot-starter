package top.itning.utils.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import top.itning.utils.email.EmailHelper;
import top.itning.utils.email.EmailProperties;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author itning
 * @date 2019/7/7 12:03
 */
@Configuration
@EnableConfigurationProperties(EmailProperties.class)
public class UtilsAutoConfigure {
    @Bean
    @ConditionalOnProperty(prefix = "util.email", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public EmailHelper emailHelper(EmailProperties emailProperties) throws IOException {
        return new EmailHelper.Builder()
                .setEmailSenderUser(emailProperties.getEmailSenderUser())
                .setEmailSenderPwd(emailProperties.getEmailSenderPwd())
                .setCarbonCopy(emailProperties.getCarbonCopyAddress())
                .setSmtpServer(emailProperties.getSmtpServer())
                .setTemplateDir(getOrDefaultTemplateDir(emailProperties))
                .build();
    }

    /**
     * 获取HTML模板目录
     * 如果{@link EmailProperties#getTemplateDir()}为空则使用classpath目录
     *
     * @param emailProperties {@link EmailProperties}
     * @return HTML模板目录
     * @throws FileNotFoundException FileNotFoundException
     * @see org.springframework.util.ResourceUtils#getFile(String)
     */
    private String getOrDefaultTemplateDir(EmailProperties emailProperties) throws FileNotFoundException {
        if (emailProperties.getTemplateDir() == null) {
            return ResourceUtils.getFile("classpath:/").getPath();
        } else {
            return emailProperties.getTemplateDir();
        }
    }
}
