/*
      Copyright (C) 2012-2014 Gabriel Van Eyck

    This software is provided 'as-is', without any express or implied
    warranty. In no event will the authors be held liable for any damages
    arising from the use of this software.

    Permission is granted to anyone to use this software for any purpose,
    including commercial applications, and to alter it and redistribute it
    freely, subject to the following restrictions:

    1. The origin of this software must not be misrepresented; you must not
    claim that you wrote the original software. If you use this software
    in a product, an acknowledgment in the product documentation would be
    appreciated but is not required.
    2. Altered source versions must be plainly marked as such, and must not be
    misrepresented as being the original software.
    3. This notice may not be removed or altered from any source distribution.

    Gabriel Van Eyck vaneyckster@gmail.com
 */
package com.gvaneyck.rtmp;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

/**
 * Trust manager which accepts certificates without any validation
 * except date validation.
 *
 * @author Gabriel Van Eyck
 */
public class DummyTrustManager implements X509TrustManager {
    public boolean isClientTrusted(X509Certificate[] cert) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] cert) {
        try {
            cert[0].checkValidity();
            return true;
        }
        catch (CertificateExpiredException e) {
            return false;
        }
        catch (CertificateNotYetValidException e) {
            return false;
        }
    }

    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Do nothing for now.
    }

    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Do nothing for now.
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
