VALIDITY=356
PASSWORD=mysecretpassword

rm -rf ./secrets

mkdir secrets

# Create a Certificate Authority public/private keypair
openssl req -new -x509 -keyout secrets/ca-key -out secrets/ca-cert -days $VALIDITY -passout pass:$PASSWORD -subj "/C=US/ST=City/L=City/O=Organization/OU=OrganizationUnit/CN=example.com"

# Create truststores for the client and server and import the CA certificate
keytool -keystore secrets/kafka.client.truststore.jks -alias CARoot -importcert -file secrets/ca-cert -storepass $PASSWORD -noprompt
keytool -keystore secrets/kafka.server.truststore.jks -alias CARoot -importcert -file secrets/ca-cert -storepass $PASSWORD -noprompt

# Create keystores with 'localhost' certificates for the client and server
keytool -keystore secrets/kafka.server.keystore.jks -alias localhost -keyalg RSA -validity $VALIDITY -genkey -storepass $PASSWORD -dname "CN=localhost, OU=OrganizationUnit, O=Organization, L=City, ST=State, C=US"
keytool -keystore secrets/kafka.client.keystore.jks -alias localhost -keyalg RSA -validity $VALIDITY -genkey -storepass $PASSWORD -dname "CN=localhost, OU=OrganizationUnit, O=Organization, L=City, ST=State, C=US"

#Export the unsigned 'localhost' certificates for client and server
keytool -keystore secrets/kafka.server.keystore.jks -alias localhost -certreq -file secrets/cert-file-server -storepass $PASSWORD -noprompt
keytool -keystore secrets/kafka.client.keystore.jks -alias localhost -certreq -file secrets/cert-file-client -storepass $PASSWORD -noprompt

# Sign the 'localhost' certificates for client and server
openssl x509 -req -CA secrets/ca-cert -CAkey secrets/ca-key -in secrets/cert-file-server -out secrets/cert-signed-server -days $VALIDITY -CAcreateserial -passin pass:$PASSWORD
openssl x509 -req -CA secrets/ca-cert -CAkey secrets/ca-key -in secrets/cert-file-client -out secrets/cert-signed-client -days $VALIDITY -CAcreateserial -passin pass:$PASSWORD

# Import the CA certifcates also into the client and server keystores
keytool -keystore secrets/kafka.server.keystore.jks -alias CARoot -importcert -file secrets/ca-cert -storepass $PASSWORD -noprompt
keytool -keystore secrets/kafka.client.keystore.jks -alias CARoot -importcert -file secrets/ca-cert -storepass $PASSWORD -noprompt

# Replace the unsigned 'localhost' certificates with the signed ones for client and server
keytool -keystore secrets/kafka.server.keystore.jks -alias localhost -importcert -file secrets/cert-signed-server -storepass $PASSWORD -noprompt
keytool -keystore secrets/kafka.client.keystore.jks -alias localhost -importcert -file secrets/cert-signed-client -storepass $PASSWORD -noprompt
