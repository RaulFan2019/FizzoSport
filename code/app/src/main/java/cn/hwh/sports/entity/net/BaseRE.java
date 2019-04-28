package cn.hwh.sports.entity.net;

import org.xutils.http.annotation.HttpResponse;

import cn.hwh.sports.network.BaseResponseParser;

/**
 * Created by Administrator on 2016/6/28.
 */
@HttpResponse(parser = BaseResponseParser.class)
public class BaseRE {

    public String result;//正确返回的内容
    public int errorcode;//错误编号
    public String errormsg;//错误描述


    public BaseRE() {
    }

    public BaseRE(String result, int errorcode, String errormsg) {
        super();
        this.result = result;
        this.errorcode = errorcode;
        this.errormsg = errormsg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }
}
