package cn.hwh.sports.ssh;


public interface ExecTaskCallbackHandler {

    void onFail();

    void onComplete(String completeString);
}
