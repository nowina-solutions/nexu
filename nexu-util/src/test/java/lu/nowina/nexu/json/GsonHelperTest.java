/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.json;

import java.io.FileInputStream;
import java.security.KeyStore.PasswordProtection;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.JKSSignatureToken;
import lu.nowina.nexu.api.CertificateFilter;
import lu.nowina.nexu.api.Execution;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.Purpose;
import lu.nowina.nexu.json.pojo.TestWithCertificate;

public class GsonHelperTest {

    @Test
    public void test1() throws Exception {

        final GetCertificateRequest req = new GetCertificateRequest();
        req.setCertificateFilter(new CertificateFilter(Purpose.SIGNATURE));
        req.setExternalId("externalId");
        req.setNonce("nonce");
        req.setRequestSeal("seal");
        req.setUserLocale("fr");

        final String json = GsonHelper.toJson(req);
        Assert.assertEquals("{\"certificateFilter\":{\"purpose\":\"SIGNATURE\",\"nonRepudiationBit\":false},\"closeToken\":true,\"userLocale\":\"fr\",\"externalId\":\"externalId\",\"requestSeal\":\"seal\",\"nonce\":\"nonce\"}", json);

    }

    @Test
    public void test2() throws Exception {

        final GetCertificateResponse resp = GsonHelper.fromJson("{\"certificate\":\"MIIDhTCCAm2gAwIBAgIEXy8mrjANBgkqhkiG9w0BAQsFADBzMRAwDgYDVQQGEwdVbmtub3duMRAw\r\nDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYDVQQKEwdVbmtub3duMRAwDgYD\r\nVQQLEwdVbmtub3duMRcwFQYDVQQDEw5EYXZpZCBOYXJhbXNraTAeFw0xNTEyMDMxNTIzMzJaFw0x\r\nNjExMjcxNTIzMzJaMHMxEDAOBgNVBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNV\r\nBAcTB1Vua25vd24xEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xFzAVBgNVBAMT\r\nDkRhdmlkIE5hcmFtc2tpMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgdtMIcLoZ2Su\r\na2IjRW8Lg6ElmEJdpEpyhTRL6Xep+fNIWB2nPFxhG+7RtDm4kSf5dPxmRJFuHhw8yO1gyZ9R7m7C\r\nCACudr9wRlkr9r96giUYrB6S0MLKRIhuqY452Ekg/iWU13AUQsvXsmOQE5SXD3vRTaKauUa8txJa\r\ncbqWbrDK4LuE/WqOuIeNc+8Sjfv06K2eE/WQsMqa32LRjp1zomT029nUnYpm1ZTq41GGhSrSLFab\r\nIYOknLdN2tbBPzmvt3qGf9q4Kx5gC35fLjZASGSmk1EXKO1huDsIcgjO1NFmHdFvqgbygPBtED63\r\ngZhAVBlpmbxUdKMLtTaz+pWf8wIDAQABoyEwHzAdBgNVHQ4EFgQUH2kJilwO3YF8FOY7AWCriSHl\r\nIBgwDQYJKoZIhvcNAQELBQADggEBAIBU1oZe03VJsdmxQCtB40eYl6W76CSD8Ik66O81YrsjfH1U\r\ntyxrGrS8gonrsC3agLqe3MTvkD99KC6GRVrQw96PKfZK2SjFsOevMxCvq3U7OHisoyDMXIUPHzae\r\nc1eDYkVsrj3d7Q4++LIJ7W+fpfY+5VZLHD+osTQGwfdDAbhbtw3sySTAXCWWnI6bJAD6JBh0SzTo\r\n82MAiGb0KnuzBPVdkuW5BmdicPnkWx4llVxG3A5deBATxtA3k1cUZNI9xzTCRFD3+h9x8PdPTtIA\r\nf4+NiKMIfG/eRWUXuYFlm9vuryL8vvXaHuYjxBXDhGmPeDWEJMSzDQJtLewGklr6MfI\u003d\r\n\"}", GetCertificateResponse.class);
        System.out.println(resp.getCertificate());

    }

    @Test
    public void test3() throws Exception {

        final JKSSignatureToken token = new JKSSignatureToken(new FileInputStream("src/test/resources/keystore.jks"), new PasswordProtection("password".toCharArray()));
        final DSSPrivateKeyEntry key = token.getKeys().get(0);
        final TestWithCertificate cert = new TestWithCertificate(key);

        final String json = GsonHelper.toJson(cert);
        System.out.println(json);
        final TestWithCertificate cert2 = GsonHelper.fromJson(json, TestWithCertificate.class);
        Assert.assertArrayEquals(cert2.getToken().getEncoded(), cert.getToken().getEncoded());

    }

    @Test
    public void test4() throws Exception {

        System.out.println(Base64.encodeBase64String("Hello World".getBytes()));
        final String json = GsonHelper.toJson("Hello World".getBytes());
        System.out.println(json);

        final String compare = GsonHelper.fromJson(json, String.class);
        System.out.println(compare);

    }

    @Test
    public void test5() throws Exception {

        final GetCertificateResponse resp = GsonHelper.fromJson("{\"certificate\":\"MIIDhTCCAm2gAwIBAgIEXy8mrjANBgkqhkiG9w0BAQsFADBzMRAwDgYDVQQGEwdVbmtub3duMRAw\r\nDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYDVQQKEwdVbmtub3duMRAwDgYD\r\nVQQLEwdVbmtub3duMRcwFQYDVQQDEw5EYXZpZCBOYXJhbXNraTAeFw0xNTEyMDMxNTIzMzJaFw0x\r\nNjExMjcxNTIzMzJaMHMxEDAOBgNVBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNV\r\nBAcTB1Vua25vd24xEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xFzAVBgNVBAMT\r\nDkRhdmlkIE5hcmFtc2tpMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgdtMIcLoZ2Su\r\na2IjRW8Lg6ElmEJdpEpyhTRL6Xep+fNIWB2nPFxhG+7RtDm4kSf5dPxmRJFuHhw8yO1gyZ9R7m7C\r\nCACudr9wRlkr9r96giUYrB6S0MLKRIhuqY452Ekg/iWU13AUQsvXsmOQE5SXD3vRTaKauUa8txJa\r\ncbqWbrDK4LuE/WqOuIeNc+8Sjfv06K2eE/WQsMqa32LRjp1zomT029nUnYpm1ZTq41GGhSrSLFab\r\nIYOknLdN2tbBPzmvt3qGf9q4Kx5gC35fLjZASGSmk1EXKO1huDsIcgjO1NFmHdFvqgbygPBtED63\r\ngZhAVBlpmbxUdKMLtTaz+pWf8wIDAQABoyEwHzAdBgNVHQ4EFgQUH2kJilwO3YF8FOY7AWCriSHl\r\nIBgwDQYJKoZIhvcNAQELBQADggEBAIBU1oZe03VJsdmxQCtB40eYl6W76CSD8Ik66O81YrsjfH1U\r\ntyxrGrS8gonrsC3agLqe3MTvkD99KC6GRVrQw96PKfZK2SjFsOevMxCvq3U7OHisoyDMXIUPHzae\r\nc1eDYkVsrj3d7Q4++LIJ7W+fpfY+5VZLHD+osTQGwfdDAbhbtw3sySTAXCWWnI6bJAD6JBh0SzTo\r\n82MAiGb0KnuzBPVdkuW5BmdicPnkWx4llVxG3A5deBATxtA3k1cUZNI9xzTCRFD3+h9x8PdPTtIA\r\nf4+NiKMIfG/eRWUXuYFlm9vuryL8vvXaHuYjxBXDhGmPeDWEJMSzDQJtLewGklr6MfI\\u003d\r\n\"}", GetCertificateResponse.class);
        final Execution<GetCertificateResponse> exec = new Execution<>(resp);

        final String json = GsonHelper.toJson(exec);
        System.out.println(json);


        final Execution<GetCertificateResponse> exec2 = GsonHelper.fromExecution(json, GetCertificateResponse.class);

        Assert.assertEquals(true, exec.isSuccess());
        Assert.assertEquals(resp.getCertificate(), exec2.getResponse().getCertificate());

    }

}
