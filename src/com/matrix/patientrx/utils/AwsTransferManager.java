package com.matrix.patientrx.utils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.matrix.patientrx.constants.Constants;

public class AwsTransferManager {
	private static AwsTransferManager awsTransferManager = null;
	private TransferManager manager = null;

	public AwsTransferManager() {
		ClientConfiguration s3Config = new ClientConfiguration();
		// Sets the maximum number of allowed open HTTP connections.
		s3Config.setMaxConnections(5);
		// Sets the amount of time to wait (in milliseconds) for data
		// to be transferred over a connection.
		s3Config.setSocketTimeout(30000);
		AWSCredentials myCredentials = new BasicAWSCredentials(
				Constants.AWS_S3_ACCESS_KEY_ID, Constants.AWS_S3_SECRET_KEY);
		AmazonS3Client s3Client = new AmazonS3Client(myCredentials, s3Config);
		manager = new TransferManager(s3Client);
	}

	public static TransferManager getTransferManager() {
		return getInstance().manager;
	}

	public static AwsTransferManager getInstance() {
		if (awsTransferManager == null) {
			awsTransferManager = new AwsTransferManager();
		}
		return awsTransferManager;
	}
}
