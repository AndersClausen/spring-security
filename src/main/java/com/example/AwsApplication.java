package com.example;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Anders Clausen
 */
@SpringBootApplication
@RestController
public class AwsApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(AwsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AwsApplication.class, args);
    }

    @GetMapping("/getCreds")
    public String getCreds() throws IOException {

        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        LOGGER.debug("access and secret keys: " + credentials.getAWSAccessKeyId() + " - " + credentials.getAWSSecretKey());

        BasicAWSCredentials creds = new BasicAWSCredentials(credentials.getAWSAccessKeyId(), credentials.getAWSSecretKey());

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(Regions.EU_WEST_1)
                .build();

        S3Object s3Object = s3Client.getObject("bacreds", "keyproperties.properties");

        String value = getValue(s3Object.getObjectContent(), "key2");

        return value;
    }

    @Null
    private static String getValue(@NotNull InputStream input, @NotNull String key)
            throws IOException {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            LOGGER.debug("Key/Value = " + line);
            if (line == null) {
                break;
            }
            if (line.contains(key)) {
                return line.substring(line.indexOf("=")+1);
            }
        }
        return null;
    }
}
