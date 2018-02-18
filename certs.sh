#!/usr/bin/env bash

echo "Downloading cert and saving to pem file"
openssl s_client -showcerts -connect api.cp.dyson.com:443 </dev/null 2>/dev/null|openssl x509 -outform PEM >api.cp.dyson.com.pem

echo "Converting to der format"
openssl x509 -outform der -in api.cp.dyson.com.pem -out api.cp.dyson.com.der

echo "Pushing into truststpre"
keytool -import -v -trustcacerts -keystore cacerts.jks -alias dysonca -file api.cp.dyson.com.der -keypass changeit -storepass changeit -noprompt

rm api.cp.dyson.com.pem
rm api.cp.dyson.com.der