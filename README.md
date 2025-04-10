# WhereMyBuckGoes Backend

WhereMyBuckGoes is a backend application designed to help you manage and track your expenses efficiently. Keep a record of your spending, categorize transactions, and gain insights into your financial habits.

**Note:** The frontend application for this backend is available at [expense_track](https://github.com/kartikeychoudhary/expense_track)

## Docker Deployment
To build the Docker image for this application, run the following command in the project root directory:
Before building the Docker image, ensure you have configured the following environment variables in your environment:

- GEMINI_API: Your encrypted Gemini API key
- GOTIFY_KEY: Your encrypted Gotify key
- GOTIFY_HOST: Your encrypted Gotify host URL
- SECRET: Your encrypted secret key
- DB_PASSWORD: Your encrypted database password
- DB_USER: Your encrypted database username
- DB_HOST: Your database host
- DB_PORT: Your database port
- DB_DATABASE: Your database name
- PASSWORD: Your encryption salt password
- GOTIFY_ENABLE: Set to 'true' or 'false'
- GENAI_ENABLE: Set to 'true' or 'false'

**Note:** Replace the placeholder values (e.g., `<your_salt_password>`) with your actual encrypted values and configuration. You can use an online Jasypt encryptor with the algorithm `PBEWithMD5AndDES` and no IV generator, using the `<your_salt_password>` as the encryption key (salt).
or you can use jasypt jar
```bash
java -cp jasypt-x.x.x.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="your_value" password=your_salt_password algorithm=PBEWithMD5AndDES ivGeneratorClassName=org.jasypt.iv.NoIvGenerator
```

You can encrypt sensitive values using an online Jasypt encryptor with the algorithm PBEWithMD5AndDES and no IV generator.
```bash
docker build -t wheremybuckgoes -f docker/dockerfile .
```
To run the container:
```bash
docker run -p 5500:5500 wheremybuckgoes
```

To run a MariaDB instance alongside the application, use the following command:
```bash
    docker run -d \
        --name mariadb \
        -e MYSQL_ROOT_PASSWORD=<your_root_password> \
        -e MYSQL_DATABASE=<your_database> \
        -e MYSQL_USER=<your_user> \
        -e MYSQL_PASSWORD=<your_password> \
        -p 3306:3306 \
        mariadb:latest
```

