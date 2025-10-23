# S3 Configuration for Room Images

## Overview
Room images are now uploaded to AWS S3 bucket instead of being stored locally. This provides better scalability, reliability, and performance for serving images.

## Configuration

### Environment Variables
The following environment variables need to be set for S3 integration to work:

- `AWS_S3_BUCKET_NAME` - The name of the S3 bucket (default: `sim-hotel-app`)
- `AWS_S3_REGION` - The AWS region where the bucket is located (default: `eu-west-1`)
- `AWS_S3_ACCESS_KEY` - AWS access key with S3 write permissions
- `AWS_S3_SECRET_KEY` - AWS secret key corresponding to the access key
- `AWS_S3_BUCKET_URL` - The base URL of the S3 bucket (default: `https://sim-hotel-app.s3.eu-west-1.amazonaws.com/`)

### Application Properties
The configuration can be found in `application.properties`:

```properties
aws.s3.bucket-name=${AWS_S3_BUCKET_NAME:sim-hotel-app}
aws.s3.region=${AWS_S3_REGION:eu-west-1}
aws.s3.access-key=${AWS_S3_ACCESS_KEY:}
aws.s3.secret-key=${AWS_S3_SECRET_KEY:}
aws.s3.bucket-url=${AWS_S3_BUCKET_URL:https://sim-hotel-app.s3.eu-west-1.amazonaws.com/}
```

## S3 Bucket Setup

### Required Permissions
The AWS IAM user/role must have the following permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:PutObjectAcl"
      ],
      "Resource": "arn:aws:s3:::sim-hotel-app/room-images/*"
    }
  ]
}
```

### Bucket Structure
Images are uploaded to the following path structure:
```
sim-hotel-app/
└── room-images/
    ├── <uuid>_<filename1>.jpg
    ├── <uuid>_<filename2>.png
    └── ...
```

### CORS Configuration
If the images are accessed from a web application, ensure the S3 bucket has appropriate CORS configuration:

```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["GET", "HEAD"],
    "AllowedOrigins": ["*"],
    "ExposeHeaders": []
  }
]
```

## Usage

When adding or updating a room, provide an image file as a multipart file. The service will:
1. Validate that the file is an image (content type starts with `image/`)
2. Generate a unique filename using UUID
3. Upload the file to S3 bucket at `room-images/<uuid>_<original-filename>`
4. Store the full S3 URL in the database

Example URL format:
```
https://sim-hotel-app.s3.eu-west-1.amazonaws.com/room-images/550e8400-e29b-41d4-a716-446655440000_room.jpg
```

## Testing

To run tests without AWS credentials, the test suite handles connection failures gracefully and focuses on validating the business logic (e.g., file type validation).

## Security

- AWS credentials are stored as environment variables and never committed to source code
- Only image files are accepted (validated by content type)
- Each file gets a unique name to prevent collisions
- CodeQL security scanning found no vulnerabilities
