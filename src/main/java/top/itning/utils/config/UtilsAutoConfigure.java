package top.itning.utils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(UtilsAutoConfigure.class);

    @Bean
    @ConditionalOnProperty(prefix = "utils.email", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public EmailHelper emailHelper(EmailProperties emailProperties) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(emailProperties.toString());
        }
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
            String path = ResourceUtils.getFile("classpath:").getPath();
            if (logger.isDebugEnabled()) {
                logger.debug("Get Classpath: {}", path);
            }
            return path;
        } else {
            return emailProperties.getTemplateDir();
        }
    }
}
