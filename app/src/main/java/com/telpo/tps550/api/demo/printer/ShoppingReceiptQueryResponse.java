package com.telpo.tps550.api.demo.printer;


import java.math.BigDecimal;
import java.util.List;


/**
 * Author： Mr.P
 * Date: 2018/8/11 11:21
 * Description: 打印小票
 */
public class ShoppingReceiptQueryResponse extends BaseResponse {


  private ReceiptOrder receiptOrder;

  public ReceiptOrder getReceiptOrder() {
    return receiptOrder;
  }

  public void setReceiptOrder(ReceiptOrder receiptOrder) {
    this.receiptOrder = receiptOrder;
  }

  /**
   * 小票信息
   */
  public static class ReceiptOrder {
    private OrderHeadInfo orderHeadInfo;
    private List<CmmdtyInfo> cmmdtyDetails;
    private Settlement settlementInfo;
    private List<PayMent> paymentInfo;

    public OrderHeadInfo getOrderHeadInfo() {
      return orderHeadInfo;
    }

    public void setOrderHeadInfo(OrderHeadInfo orderHeadInfo) {
      this.orderHeadInfo = orderHeadInfo;
    }

    public List<CmmdtyInfo> getCmmdtyDetails() {
      return cmmdtyDetails;
    }

    public void setCmmdtyDetails(List<CmmdtyInfo> cmmdtyDetails) {
      this.cmmdtyDetails = cmmdtyDetails;
    }

    public Settlement getSettlementInfo() {
      return settlementInfo;
    }

    public void setSettlementInfo(Settlement settlementInfo) {
      this.settlementInfo = settlementInfo;
    }

    public List<PayMent> getPaymentInfo() {
      return paymentInfo;
    }

    public void setPaymentInfo(List<PayMent> paymentInfo) {
      this.paymentInfo = paymentInfo;
    }
  }

  /**
   * 订单头
   */
  public static class OrderHeadInfo {

    private String bizType;//			业态类型	"1，电器门店，2，极物，3，BIU24，4，红孩子业务类型，5.
    // 超市检查门店开关参数MDYT，若没有配置参数，或者参数为空，返回类型为1。其他根据实际配置返回门店业务类型。客户端根据不同的返回值，显示不同的门店logo图片"
    private String orderTime;//			订单时间	订单创建时间，如12:12:12
    private String orderDate;//			订单日期	订单创建时间，如2018-2-12
    private String customerMobile;//	联系方式	123XXXX5678
    private String memberCode;//		会员编号	订单记录的会员卡号信息，如025300486988
    private String memberType;//		会员类型	"调会员查询会员卡类型，如果卡类型为H活动卡，返回1非活动卡，返回空"

    private String memberName;//		会员名称	如果是活动卡，会员名称返回“顾客”，脱敏
    private String cashierCode;//		收银员号	"其他返回订单记录的收银员工号，如Y76110001 如果查询类型是2，返回“小Biu”如果查询类型是3，返回“自助付款”"

    private String fetchCode;//		    取货号	"查询类型为2,：BIU24专用，大X单号支付单号的后4位，其他查询类型返回为空"

    private String orderRemark;//		订单备注	先不取
    private String elecInvoiceRemark;//	电子发票备注	模板写死，不传值
    private String storeCode;//			门店编码	订单所属门店编码，如7611
    private String storeName;//			门店名称	门店的名称，如南京山西路店
    private String storeAddr;//			门店地址	门店的地址信息，如南京市山西路188号，NSFSALE调门店基本信息查询
    private String storePhone;//		门店电话	门店联系电话，如025-37298，NSFSALE调门店基本信息查询
    private String payOrderId;//		支付单号	"查询类型为1，返回大合并支付单号，如MA00000001，查询类型为2和3，快销业务返回大的X单号"

    private String qrCodeYi;//			二维码地址1
    private String qrCodeYiDescription;//二维码对应说明1
    private String qrCodePb;//			二维码地址2
    private String qrCodePbDescription;//二维码对应说明2
    private String khname;

    public String getBizType() {
      return bizType;
    }

    public void setBizType(String bizType) {
      this.bizType = bizType;
    }

    public String getOrderTime() {
      return orderTime;
    }

    public void setOrderTime(String orderTime) {
      this.orderTime = orderTime;
    }

    public String getOrderDate() {
      return orderDate;
    }

    public void setOrderDate(String orderDate) {
      this.orderDate = orderDate;
    }

    public String getCustomerMobile() {
      return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
      this.customerMobile = customerMobile;
    }

    public String getMemberCode() {
      return memberCode;
    }

    public void setMemberCode(String memberCode) {
      this.memberCode = memberCode;
    }

    public String getMemberType() {
      return memberType;
    }

    public void setMemberType(String memberType) {
      this.memberType = memberType;
    }

    public String getMemberName() {
      return memberName;
    }

    public void setMemberName(String memberName) {
      this.memberName = memberName;
    }

    public String getCashierCode() {
      return cashierCode;
    }

    public void setCashierCode(String cashierCode) {
      this.cashierCode = cashierCode;
    }

    public String getFetchCode() {
      return fetchCode;
    }

    public void setFetchCode(String fetchCode) {
      this.fetchCode = fetchCode;
    }

    public String getOrderRemark() {
      return orderRemark;
    }

    public void setOrderRemark(String orderRemark) {
      this.orderRemark = orderRemark;
    }

    public String getElecInvoiceRemark() {
      return elecInvoiceRemark;
    }

    public void setElecInvoiceRemark(String elecInvoiceRemark) {
      this.elecInvoiceRemark = elecInvoiceRemark;
    }

    public String getStoreCode() {
      return storeCode;
    }

    public void setStoreCode(String storeCode) {
      this.storeCode = storeCode;
    }

    public String getStoreName() {
      return storeName;
    }

    public void setStoreName(String storeName) {
      this.storeName = storeName;
    }

    public String getStoreAddr() {
      return storeAddr;
    }

    public void setStoreAddr(String storeAddr) {
      this.storeAddr = storeAddr;
    }

    public String getStorePhone() {
      return storePhone;
    }

    public void setStorePhone(String storePhone) {
      this.storePhone = storePhone;
    }

    public String getPayOrderId() {
      return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
      this.payOrderId = payOrderId;
    }

    public String getQrCodeYi() {
      return qrCodeYi;
    }

    public void setQrCodeYi(String qrCodeYi) {
      this.qrCodeYi = qrCodeYi;
    }

    public String getQrCodeYiDescription() {
      return qrCodeYiDescription;
    }

    public void setQrCodeYiDescription(String qrCodeYiDescription) {
      this.qrCodeYiDescription = qrCodeYiDescription;
    }

    public String getQrCodePb() {
      return qrCodePb;
    }

    public void setQrCodePb(String qrCodePb) {
      this.qrCodePb = qrCodePb;
    }

    public String getQrCodePbDescription() {
      return qrCodePbDescription;
    }

    public void setQrCodePbDescription(String qrCodePbDescription) {
      this.qrCodePbDescription = qrCodePbDescription;
    }

    public String getKhname() {
      return khname;
    }

    public void setKhname(String khname) {
      this.khname = khname;
    }
  }

  /**
   * 商品信息
   */
  public static class CmmdtyInfo {

    private String cmmdtyName;//			 商品名称
    private String cmmdtyCode;//             商品编码(外租商户打印需要)
    private String invoiceType;//			 发票类型	"a) 查询类型为1，返回订单对应的发票类型：01普通，02增值税，03普票电子票，04电子票 b) 查询类型为2和3，固定返回01普票"
    private String orderType;//			     订单类型	"a) 查询类型为1，返回1,普通电器，2，非订单，3，事后延保，4时候促销品，5.租赁订单b)
    // 查询类型为2和3：返回sap订单类型，自营商品：ZOR7联营商品：CSLY外租商品：YDDS"
    private String posOrderNo;//			 POS单号	"a) 查询类型为1，返回POS单号如DA0000001，b) 查询类型为2和3，返回X单号+行号"
    private String omsOrderNo;//			 OMS订单行号
    private BigDecimal price;//			     单价	"a) 商品单价，参考价b) 如果全额用券，单价=原单价+0.01c) 含优惠和满减 d) 赠品单价给0"
    private BigDecimal quantity;//		     数量	"a) 如果查询类型是2，如果商品类型是“称重商品”数量为1，b) 其他数量为商品实际数量，"
    private BigDecimal sum;//                金额	等于单价乘以数量
    private String checkCode;//				 校验码	"a) 如果查询类型是1，赠品 和非订单不返回，其他返回提货吗 b) 如果查询类型是2和3. 不返回提货吗"
    private Settlement settlement;//	     结算信息
    private List<ChildCmmdtyInfo> cmmdtyInfo;

    public String getCmmdtyName() {
      return cmmdtyName;
    }

    public void setCmmdtyName(String cmmdtyName) {
      this.cmmdtyName = cmmdtyName;
    }

    public String getInvoiceType() {
      return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
      this.invoiceType = invoiceType;
    }

    public String getOrderType() {
      return orderType;
    }

    public void setOrderType(String orderType) {
      this.orderType = orderType;
    }

    public String getPosOrderNo() {
      return posOrderNo;
    }

    public void setPosOrderNo(String posOrderNo) {
      this.posOrderNo = posOrderNo;
    }

    public String getOmsOrderNo() {
      return omsOrderNo;
    }

    public void setOmsOrderNo(String omsOrderNo) {
      this.omsOrderNo = omsOrderNo;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public void setPrice(BigDecimal price) {
      this.price = price;
    }

    public BigDecimal getQuantity() {
      return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
      this.quantity = quantity;
    }

    public BigDecimal getSum() {
      return sum;
    }

    public void setSum(BigDecimal sum) {
      this.sum = sum;
    }

    public String getCheckCode() {
      return checkCode;
    }

    public void setCheckCode(String checkCode) {
      this.checkCode = checkCode;
    }

    public Settlement getSettlement() {
      return settlement;
    }

    public void setSettlement(Settlement settlement) {
      this.settlement = settlement;
    }

    public List<ChildCmmdtyInfo> getCmmdtyInfo() {
      return cmmdtyInfo;
    }

    public void setCmmdtyInfo(List<ChildCmmdtyInfo> cmmdtyInfo) {
      this.cmmdtyInfo = cmmdtyInfo;
    }

    public String getCmmdtyCode() {
      return cmmdtyCode;
    }

    public void setCmmdtyCode(String cmmdtyCode) {
      this.cmmdtyCode = cmmdtyCode;
    }

    @Override
    public String toString() {
      return "CmmdtyInfo{" +
              "cmmdtyName='" + cmmdtyName + '\'' +
              ", cmmdtyCode='" + cmmdtyCode + '\'' +
              ", invoiceType='" + invoiceType + '\'' +
              ", orderType='" + orderType + '\'' +
              ", posOrderNo='" + posOrderNo + '\'' +
              ", omsOrderNo='" + omsOrderNo + '\'' +
              ", price=" + price +
              ", quantity=" + quantity +
              ", sum=" + sum +
              ", checkCode='" + checkCode + '\'' +
              ", settlement=" + settlement +
              ", cmmdtyInfo=" + cmmdtyInfo +
              '}';
    }
  }

  public static class ChildCmmdtyInfo {
    private String cmmdtyName;//
    private String omsOrderNo;//
    private String posOrderNo;//
    private BigDecimal quantity;//
    private String checkCode;//

    public String getCmmdtyName() {
      return cmmdtyName;
    }

    public void setCmmdtyName(String cmmdtyName) {
      this.cmmdtyName = cmmdtyName;
    }

    public String getOmsOrderNo() {
      return omsOrderNo;
    }

    public void setOmsOrderNo(String omsOrderNo) {
      this.omsOrderNo = omsOrderNo;
    }

    public String getPosOrderNo() {
      return posOrderNo;
    }

    public void setPosOrderNo(String posOrderNo) {
      this.posOrderNo = posOrderNo;
    }

    public BigDecimal getQuantity() {
      return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
      this.quantity = quantity;
    }

    public String getCheckCode() {
      return checkCode;
    }

    public void setCheckCode(String checkCode) {
      this.checkCode = checkCode;
    }
  }

  /**
   * 结算信息
   */
  public static class Settlement {
    private BigDecimal total;//			    总计金额	商品单价乘以数量的合计金额+运费
    private BigDecimal discount;//			优惠金额	等于总计金额-实收金额-用券金额-用积分金额
    private BigDecimal coupons;//			用券抵扣	用券金额合计（等于6901支付方式之和）
    private BigDecimal points;//			积分抵扣	使用积分抵扣合计金额
    private BigDecimal actual;//			实收金额	顾客付款金额（等于除了6901支付方式之和）
    private BigDecimal transportPrice;//	运费 运费金额
    private BigDecimal returnVoucher;//返券金额	该支付方式返回的券金额

    public BigDecimal getTotal() {
      return total;
    }

    public void setTotal(BigDecimal total) {
      this.total = total;
    }

    public BigDecimal getDiscount() {
      return discount;
    }

    public void setDiscount(BigDecimal discount) {
      this.discount = discount;
    }

    public BigDecimal getCoupons() {
      return coupons;
    }

    public void setCoupons(BigDecimal coupons) {
      this.coupons = coupons;
    }

    public BigDecimal getPoints() {
      return points;
    }

    public void setPoints(BigDecimal points) {
      this.points = points;
    }

    public BigDecimal getActual() {
      return actual;
    }

    public void setActual(BigDecimal actual) {
      this.actual = actual;
    }

    public BigDecimal getTransportPrice() {
      return transportPrice;
    }

    public void setTransportPrice(BigDecimal transportPrice) {
      this.transportPrice = transportPrice;
    }

    public BigDecimal getReturnVoucher() {
      return returnVoucher;
    }

    public void setReturnVoucher(BigDecimal returnVoucher) {
      this.returnVoucher = returnVoucher;
    }
  }

  /**
   * 支付信息
   */
  public static class PayMent {
    private String paymentName;//			支付方式名称	支付方式名称，如果是2266，根据第三方支付方式类型返回，如支付宝
    private BigDecimal paymentAmount;//		支付金额	支付方式对应的支付金额
//        private BigDecimal returnVoucher;//		返券金额	该支付方式返回的券金额

    public String getPaymentName() {
      return paymentName;
    }

    public void setPaymentName(String paymentName) {
      this.paymentName = paymentName;
    }

    public BigDecimal getPaymentAmount() {
      return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
      this.paymentAmount = paymentAmount;
    }

  }


}
