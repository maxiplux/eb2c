package app.quantun.eb2c.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.accessKey:#{null}}")
    private String accessKey;

    @Value("${aws.secretKey:#{null}}")
    private String secretKey;

    /**
     * Creates a CognitoIdentityProviderClient bean.
     * 
     * This method configures the AWS Cognito client based on the provided AWS credentials.
     * If the access key and secret key are provided, it uses them to create a static credentials provider.
     * Otherwise, it falls back to the default credentials provider chain.
     * 
     * @return CognitoIdentityProviderClient instance
     */
    @Bean
    public CognitoIdentityProviderClient cognitoClient() {
        if (accessKey != null && secretKey != null) {
            // Create credentials provider
            AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
            );

            // Create client with credentials
            return CognitoIdentityProviderClient.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(credentialsProvider)
                    .build();
        } else {
            // Use default provider chain
            return CognitoIdentityProviderClient.builder()
                    .region(Region.of(awsRegion))
                    .build();
        }
    }
}
