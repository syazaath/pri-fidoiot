package org.fidoalliance.fdo.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Builds a self-signed certificate chain.
 */
public class CertChainBuilder {

  private PrivateKey privateKey;
  private Certificate[] issuerChain = new X509Certificate[0];
  private SubjectPublicKeyInfo publicKeyInfo;
  private Provider provider;
  private String signatureAlgorithm;
  private X500Name subject;
  private int validityDays;
  private GeneralNames subjectAlternateNames;


  public CertChainBuilder setPrivateKey(PrivateKey privateKey) {
    this.privateKey = privateKey;
    return this;
  }

  public CertChainBuilder setIssuerChain(Certificate[] issuerChain) {
    this.issuerChain = issuerChain;
    return this;
  }

  public CertChainBuilder setPublicKey(SubjectPublicKeyInfo publicKeyInfo) {
    this.publicKeyInfo = publicKeyInfo;
    return this;
  }

  public CertChainBuilder setPublicKey(PublicKey publicKey) {
    this.publicKeyInfo =
        SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
    return this;
  }

  public CertChainBuilder setSignatureAlgorithm(String signatureAlgorithm) {
    this.signatureAlgorithm = signatureAlgorithm;
    return this;
  }

  public CertChainBuilder setSignatureAlgorithm(ASN1ObjectIdentifier algorithm) {
    this.signatureAlgorithm = new DefaultAlgorithmNameFinder().getAlgorithmName(algorithm);
    return this;
  }

  public CertChainBuilder setProvider(Provider provider) {
    this.provider = provider;
    return this;
  }

  public CertChainBuilder setSubject(String subject) {
    this.subject = new X500Name(subject);
    return this;
  }

  public CertChainBuilder setSubject(X500Name subject) {
    this.subject = subject;
    return this;
  }

  public CertChainBuilder setValidityDays(int days) {
    this.validityDays = days;
    return this;
  }

  public CertChainBuilder setSubjectAlternateNames(GeneralNames names) {
    this.subjectAlternateNames = names;
    return this;
  }

  public Certificate[] build() throws IOException {

    final Instant now = Instant.now();
    final Date notBefore = Date.from(now);
    final Date notAfter = Date.from(now.plus(Duration.ofDays(validityDays)));

    X500Name issuer = subject;
    if (issuerChain.length > 0) {
      try {
        issuer = new X509CertificateHolder(issuerChain[0].getEncoded()).getSubject();
      } catch (CertificateEncodingException e) {
        throw new IOException(e);
      }
    }

    final BigInteger serial =
        BigInteger.valueOf(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);

    final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
        issuer,
        serial,
        Date.from(Instant.now()),
        Date.from(ZonedDateTime.now().plusDays(validityDays).toInstant()),
        subject,
        publicKeyInfo);

    if (subjectAlternateNames != null) {
      certBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAlternateNames);
    }

    final JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder(
        signatureAlgorithm);

    try {
      final ContentSigner signer = jcaContentSignerBuilder.build(privateKey);
      final CertificateFactory cf = CertificateFactory.getInstance(
          "X.509", provider);

      final byte[] certBytes = certBuilder.build(signer).getEncoded();

      final X509Certificate cert =
          (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));

      final Certificate[] chain = new Certificate[issuerChain.length + 1];
      chain[0] = cert;
      for (int i = 0; i < issuerChain.length; i++) {
        chain[i + 1] = issuerChain[i];
      }

      return chain;
    } catch (CertificateException | OperatorCreationException e) {
      throw new IOException(e);
    }
  }
}