# Two Way TLS API Client Demo

Exemplo de conexão Two Way TLS

Leia mais sobre TLS aqui: https://hpbn.co/transport-layer-security-tls/#optimizing-for-tls
Veja mais detalhes sobre implementação aqui: https://github.com/Hakky54/mutual-tls-ssl

## Preparando o Ambiente

Os arquivos .jks e .cer devem ficar no classpath.

client.cer e server.cer são certicados do cliente e servidor, respectivamente.


identity.jks e truststore.jks são arquivos com keys para certificados do cliente ou certificados do servidor


Em uma comunicação Two Way TLS, o cliente e o servidor precisam trocar certificados,
então ambos devem criar um certificado e enviar para sua contra-parte que irá incluí-lo no truststore.

### Criar public/private key para o cliente

```
keytool -v -genkeypair -dname "CN=Willams,OU=Brasil,O=Dev,C=BR" -keystore ./identity.jks -storepass secret -keypass secret -keyalg RSA -keysize 2048 -alias client -validity 3650 -deststoretype pkcs12 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth
```

### Criar certificado

client.cer será enviado para o servidor

```
keytool -v -exportcert -file ./client.cer -alias client -keystore ./identity.jks -storepass secret -rfc
```

### Incluir cerficado do server no truststore

server.cer será obtido do servidor

```
keytool -v -importcert -file ./server.cer -alias server -keystore ./truststore.jks -storepass secret -noprompt
```

## Testando

Esse demo é uma aplicação spring-boot padrão. Tem uma 

Ao executar a aplicação com 
```
mvn spring-boot:run
```

Veremos a mensagem:

```
Recebido do servidor: Hello, 1
```
