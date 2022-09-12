package com.telpo.tps550.api.demo.printer;

/**
 * 响应基类
 * 名称：    BaseResponse
 *
 * @author 17093029
 *         创建日期：    2018/7/23
 *         包名： com.cnsuning.mobile.apos.library.http
 */
public class BaseResponse {

  public static final String SUCCESS = "0";
  public static final String FAILURE = "1";

  public static final String SUCCESS_Y = "Y";
  public static final String FAILURE_N = "N";

  public String flag;        //0：成功  1：失败
  public String errCode = "";     //错误编码
  public String errMsg = "";      //错误信息


  public BaseResponse() {

  }
}
