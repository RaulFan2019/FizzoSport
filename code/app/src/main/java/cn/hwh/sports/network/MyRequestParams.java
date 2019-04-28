package cn.hwh.sports.network;

import android.content.Context;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.app.DefaultParamsBuilder;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.hwh.sports.LocalApplication;
import cn.hwh.sports.config.UrlConfig;
import cn.hwh.sports.entity.db.UserDE;

/**
 * Created by Raul on 2015/11/12.
 * 网络传输对象
 */
public class MyRequestParams extends RequestParams {

    //头信息的Key
    private static final String USER_AGENT = "user-agent";


    public MyRequestParams(Context context, String url) {
        super(url);
        //创建头信息
        this.addHeader(USER_AGENT, UrlConfig.USER_ANGENT);
        UserDE user = LocalApplication.getInstance().getLoginUser(context);
        if (user != null) {
            this.addBodyParameter("sessionid", user.sessionId);
            this.addBodyParameter("userid", String.valueOf(user.userId));
        } else {
            this.addBodyParameter("sessionid", "14");
            this.addBodyParameter("userid", "14");
        }
        this.setSslSocketFactory(trustAllSSlSocketFactory);
    }

    private static SSLSocketFactory trustAllSSlSocketFactory;

    public static SSLSocketFactory getTrustAllSSLSocketFactory() {
        if (trustAllSSlSocketFactory == null) {
            synchronized (DefaultParamsBuilder.class) {
                if (trustAllSSlSocketFactory == null) {

                    // 信任所有证书
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }};
                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, trustAllCerts, null);
                        trustAllSSlSocketFactory = sslContext.getSocketFactory();
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        }
        return trustAllSSlSocketFactory;
    }


}
