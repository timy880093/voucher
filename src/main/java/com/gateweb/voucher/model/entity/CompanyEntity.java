/*
 * $Header: $
 * This java source file is generated by pkliu on Mon Oct 30 14:37:48 CST 2017
 * For more information, please contact pkliu@sysfoundry.com
 */
package com.gateweb.voucher.model.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author pkliu
 */
@Entity
@Table(name = "company")
public class CompanyEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer companyId;

  @Transient public Boolean online;

  @Column(name = "company_type")
  public Integer companyType;

  @Column(name = "code_name")
  public String codeName;

  @Column(name = "company_address")
  public String companyAddress;

  @Column(name = "email_2")
  public String email2;

  @Column(name = "email_1")
  public String email1;

  @Column(name = "tax_no")
  public String taxNo;

  @Column(name = "contact_phone1")
  public String contactPhone1;

  @Column(name = "contact_phone2")
  public String contactPhone2;

  @Column(name = "bottom_banner")
  public String bottomBanner;

  @Column(name = "verify_status")
  public Integer verifyStatus;

  @Column(name = "fax")
  public String fax;

  @Column(name = "create_date")
  public Timestamp createDate;

  @Column(name = "business_no")
  public String businessNo;

  @Column(name = "company_key")
  public String companyKey;

  @Column(name = "mailing_address")
  public String mailingAddress;

  @Column(name = "phone")
  public String phone;

  @Column(name = "parent_id")
  public Integer parentId;

  @Column(name = "name")
  public String name;

  @Column(name = "creator_id")
  public Integer creatorId;

  @Column(name = "modifier_id")
  public Integer modifierId;

  /** #1273 remove getusername and remove companyvo and add two transient in entity */
  @Transient public String creator;

  /** #1273 remove getusername and remove companyvo and add two transient in entity */
  @Transient public String modifier;

  @Column(name = "unauthorized_date")
  public Timestamp unauthorizedDate;

  @Column(name = "tax_office")
  public String taxOffice;

  @Column(name = "top_banner")
  public String topBanner;

  @Column(name = "transfer_type")
  public Integer transferType;

  @Column(name = "contact1")
  public String contact1;

  @Column(name = "modify_date")
  public Timestamp modifyDate;

  @Column(name = "print_service_type")
  public Integer printServiceType;

  @Column(name = "contact2")
  public String contact2;

  @Column(name = "city_id")
  public Integer cityId;

  @Column(name = "pdf_user_password")
  public String pdfUserPassword;

  @Column(name = "pdf_owner_password")
  public String pdfOwnerPassword;

  @Column(name = "send_accounting_documents")
  public Integer sendAccountingDocuments;

  @Column(name = "send_winning_letters")
  public Integer sendWinningLetters;

  @Column(name = "lock_pdf_of_email")
  public Boolean lockPdfOfEmail;

  @Column(name = "send_interface_accounting_documents")
  public Integer sendInterfaceAccountingDocuments;

  // 是否下載回復擋
  @Column(name = "enable_download_reply")
  public Boolean enableDownloadReply = true;

  // 是否寄送錯誤通知信
  @Column(name = "enable_send_error_mail")
  public Boolean enableSendErrorMail = true;

  // 是否寄送折讓通知單一般使用 value -> null 0:不寄送 1:寄送(收費) 2:寄送(不收費) 3:寄送(收費客製化)
  @Column(name = "send_allowance_accounting_documents")
  public Integer sendAllowanceAccountingDocuments;

  // 是否寄送折讓通知單介接使用 value -> null 0:不寄送 1:寄送(收費) 2:寄送(不收費) 3:寄送(收費客製化)
  @Column(name = "send_interface_allowance_accounting_documents")
  public Integer sendInterfaceAllowanceAccountingDocuments;

  public Integer getCompanyId() {
    return companyId;
  }

  public CompanyEntity setCompanyId(Integer companyId) {
    this.companyId = companyId;
    return this;
  }

  public Boolean getOnline() {
    return online;
  }

  public CompanyEntity setOnline(Boolean online) {
    this.online = online;
    return this;
  }

  public Integer getCompanyType() {
    return companyType;
  }

  public CompanyEntity setCompanyType(Integer companyType) {
    this.companyType = companyType;
    return this;
  }

  public String getCodeName() {
    return codeName;
  }

  public CompanyEntity setCodeName(String codeName) {
    this.codeName = codeName;
    return this;
  }

  public String getCompanyAddress() {
    return companyAddress;
  }

  public CompanyEntity setCompanyAddress(String companyAddress) {
    this.companyAddress = companyAddress;
    return this;
  }

  public String getEmail2() {
    return email2;
  }

  public CompanyEntity setEmail2(String email2) {
    this.email2 = email2;
    return this;
  }

  public String getEmail1() {
    return email1;
  }

  public CompanyEntity setEmail1(String email1) {
    this.email1 = email1;
    return this;
  }

  public String getTaxNo() {
    return taxNo;
  }

  public CompanyEntity setTaxNo(String taxNo) {
    this.taxNo = taxNo;
    return this;
  }

  public String getContactPhone1() {
    return contactPhone1;
  }

  public CompanyEntity setContactPhone1(String contactPhone1) {
    this.contactPhone1 = contactPhone1;
    return this;
  }

  public String getContactPhone2() {
    return contactPhone2;
  }

  public CompanyEntity setContactPhone2(String contactPhone2) {
    this.contactPhone2 = contactPhone2;
    return this;
  }

  public String getBottomBanner() {
    return bottomBanner;
  }

  public CompanyEntity setBottomBanner(String bottomBanner) {
    this.bottomBanner = bottomBanner;
    return this;
  }

  public Integer getVerifyStatus() {
    return verifyStatus;
  }

  public CompanyEntity setVerifyStatus(Integer verifyStatus) {
    this.verifyStatus = verifyStatus;
    return this;
  }

  public String getFax() {
    return fax;
  }

  public CompanyEntity setFax(String fax) {
    this.fax = fax;
    return this;
  }

  public Timestamp getCreateDate() {
    return createDate;
  }

  public CompanyEntity setCreateDate(Timestamp createDate) {
    this.createDate = createDate;
    return this;
  }

  public String getBusinessNo() {
    return businessNo;
  }

  public CompanyEntity setBusinessNo(String businessNo) {
    this.businessNo = businessNo;
    return this;
  }

  public String getCompanyKey() {
    return companyKey;
  }

  public CompanyEntity setCompanyKey(String companyKey) {
    this.companyKey = companyKey;
    return this;
  }

  public String getMailingAddress() {
    return mailingAddress;
  }

  public CompanyEntity setMailingAddress(String mailingAddress) {
    this.mailingAddress = mailingAddress;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public CompanyEntity setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public Integer getParentId() {
    return parentId;
  }

  public CompanyEntity setParentId(Integer parentId) {
    this.parentId = parentId;
    return this;
  }

  public String getName() {
    return name;
  }

  public CompanyEntity setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getCreatorId() {
    return creatorId;
  }

  public CompanyEntity setCreatorId(Integer creatorId) {
    this.creatorId = creatorId;
    return this;
  }

  public Integer getModifierId() {
    return modifierId;
  }

  public CompanyEntity setModifierId(Integer modifierId) {
    this.modifierId = modifierId;
    return this;
  }

  public String getCreator() {
    return creator;
  }

  public CompanyEntity setCreator(String creator) {
    this.creator = creator;
    return this;
  }

  public String getModifier() {
    return modifier;
  }

  public CompanyEntity setModifier(String modifier) {
    this.modifier = modifier;
    return this;
  }

  public Timestamp getUnauthorizedDate() {
    return unauthorizedDate;
  }

  public CompanyEntity setUnauthorizedDate(Timestamp unauthorizedDate) {
    this.unauthorizedDate = unauthorizedDate;
    return this;
  }

  public String getTaxOffice() {
    return taxOffice;
  }

  public CompanyEntity setTaxOffice(String taxOffice) {
    this.taxOffice = taxOffice;
    return this;
  }

  public String getTopBanner() {
    return topBanner;
  }

  public CompanyEntity setTopBanner(String topBanner) {
    this.topBanner = topBanner;
    return this;
  }

  public Integer getTransferType() {
    return transferType;
  }

  public CompanyEntity setTransferType(Integer transferType) {
    this.transferType = transferType;
    return this;
  }

  public String getContact1() {
    return contact1;
  }

  public CompanyEntity setContact1(String contact1) {
    this.contact1 = contact1;
    return this;
  }

  public Timestamp getModifyDate() {
    return modifyDate;
  }

  public CompanyEntity setModifyDate(Timestamp modifyDate) {
    this.modifyDate = modifyDate;
    return this;
  }

  public Integer getPrintServiceType() {
    return printServiceType;
  }

  public CompanyEntity setPrintServiceType(Integer printServiceType) {
    this.printServiceType = printServiceType;
    return this;
  }

  public String getContact2() {
    return contact2;
  }

  public CompanyEntity setContact2(String contact2) {
    this.contact2 = contact2;
    return this;
  }

  public Integer getCityId() {
    return cityId;
  }

  public CompanyEntity setCityId(Integer cityId) {
    this.cityId = cityId;
    return this;
  }

  public String getPdfUserPassword() {
    return pdfUserPassword;
  }

  public CompanyEntity setPdfUserPassword(String pdfUserPassword) {
    this.pdfUserPassword = pdfUserPassword;
    return this;
  }

  public String getPdfOwnerPassword() {
    return pdfOwnerPassword;
  }

  public CompanyEntity setPdfOwnerPassword(String pdfOwnerPassword) {
    this.pdfOwnerPassword = pdfOwnerPassword;
    return this;
  }

  public Integer getSendAccountingDocuments() {
    return sendAccountingDocuments;
  }

  public CompanyEntity setSendAccountingDocuments(Integer sendAccountingDocuments) {
    this.sendAccountingDocuments = sendAccountingDocuments;
    return this;
  }

  public Integer getSendWinningLetters() {
    return sendWinningLetters;
  }

  public CompanyEntity setSendWinningLetters(Integer sendWinningLetters) {
    this.sendWinningLetters = sendWinningLetters;
    return this;
  }

  public Boolean getLockPdfOfEmail() {
    return lockPdfOfEmail;
  }

  public CompanyEntity setLockPdfOfEmail(Boolean lockPdfOfEmail) {
    this.lockPdfOfEmail = lockPdfOfEmail;
    return this;
  }

  public Integer getSendInterfaceAccountingDocuments() {
    return sendInterfaceAccountingDocuments;
  }

  public CompanyEntity setSendInterfaceAccountingDocuments(
      Integer sendInterfaceAccountingDocuments) {
    this.sendInterfaceAccountingDocuments = sendInterfaceAccountingDocuments;
    return this;
  }

  public Boolean getEnableDownloadReply() {
    return enableDownloadReply;
  }

  public CompanyEntity setEnableDownloadReply(Boolean enableDownloadReply) {
    this.enableDownloadReply = enableDownloadReply;
    return this;
  }

  public Boolean getEnableSendErrorMail() {
    return enableSendErrorMail;
  }

  public CompanyEntity setEnableSendErrorMail(Boolean enableSendErrorMail) {
    this.enableSendErrorMail = enableSendErrorMail;
    return this;
  }

  public Integer getSendAllowanceAccountingDocuments() {
    return sendAllowanceAccountingDocuments;
  }

  public CompanyEntity setSendAllowanceAccountingDocuments(
      Integer sendAllowanceAccountingDocuments) {
    this.sendAllowanceAccountingDocuments = sendAllowanceAccountingDocuments;
    return this;
  }

  public Integer getSendInterfaceAllowanceAccountingDocuments() {
    return sendInterfaceAllowanceAccountingDocuments;
  }

  public CompanyEntity setSendInterfaceAllowanceAccountingDocuments(
      Integer sendInterfaceAllowanceAccountingDocuments) {
    this.sendInterfaceAllowanceAccountingDocuments = sendInterfaceAllowanceAccountingDocuments;
    return this;
  }

  public CompanyEntity() {}

  public CompanyEntity(
      Integer companyType,
      String codeName,
      String companyAddress,
      String email2,
      String email1,
      String taxNo,
      String contactPhone1,
      String contactPhone2,
      String bottomBanner,
      Integer verifyStatus,
      String fax,
      Timestamp createDate,
      Integer companyId,
      String businessNo,
      String companyKey,
      String mailingAddress,
      String phone,
      Integer parentId,
      String name,
      Integer creatorId,
      Integer modifierId,
      String taxOffice,
      String topBanner,
      Integer transferType,
      String contact1,
      Timestamp modifyDate,
      Timestamp unauthorizedDate,
      Integer printServiceType,
      String contact2,
      Integer cityId,
      String pdfUserPassword,
      String pdfOwnerPassword,
      Integer sendAccountingDocuments,
      Integer sendWinningLetters,
      Boolean lockPdfOfEmail,
      Integer sendInterfaceAccountingDocuments) {
    this.setCompanyType(companyType);
    this.setCodeName(codeName);
    this.setCompanyAddress(companyAddress);
    this.setEmail2(email2);
    this.setEmail1(email1);
    this.setTaxNo(taxNo);
    this.setContactPhone1(contactPhone1);
    this.setContactPhone2(contactPhone2);
    this.setBottomBanner(bottomBanner);
    this.setVerifyStatus(verifyStatus);
    this.setFax(fax);
    this.setCreateDate(createDate);
    this.setCompanyId(companyId);
    this.setBusinessNo(businessNo);
    this.setCompanyKey(companyKey);
    this.setMailingAddress(mailingAddress);
    this.setPhone(phone);
    this.setParentId(parentId);
    this.setName(name);
    this.setCreatorId(creatorId);
    this.setModifierId(modifierId);
    this.setTaxOffice(taxOffice);
    this.setTopBanner(topBanner);
    this.setTransferType(transferType);
    this.setContact1(contact1);
    this.setModifyDate(modifyDate);
    this.setPrintServiceType(printServiceType);
    this.setContact2(contact2);
    this.setCityId(cityId);
    this.setUnauthorizedDate(unauthorizedDate);
    this.setPdfUserPassword(pdfUserPassword);
    this.setPdfOwnerPassword(pdfOwnerPassword);
    this.setSendAccountingDocuments(sendAccountingDocuments);
    this.setSendWinningLetters(sendWinningLetters);
    this.setLockPdfOfEmail(lockPdfOfEmail);
    this.setSendInterfaceAccountingDocuments(sendInterfaceAccountingDocuments);
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("CompanyEntity{");
    sb.append("companyId=").append(companyId);
    sb.append(", online=").append(online);
    sb.append(", companyType=").append(companyType);
    sb.append(", codeName='").append(codeName).append('\'');
    sb.append(", companyAddress='").append(companyAddress).append('\'');
    sb.append(", email2='").append(email2).append('\'');
    sb.append(", email1='").append(email1).append('\'');
    sb.append(", taxNo='").append(taxNo).append('\'');
    sb.append(", contactPhone1='").append(contactPhone1).append('\'');
    sb.append(", contactPhone2='").append(contactPhone2).append('\'');
    sb.append(", bottomBanner='").append(bottomBanner).append('\'');
    sb.append(", verifyStatus=").append(verifyStatus);
    sb.append(", fax='").append(fax).append('\'');
    sb.append(", createDate=").append(createDate);
    sb.append(", businessNo='").append(businessNo).append('\'');
    sb.append(", companyKey='").append(companyKey).append('\'');
    sb.append(", mailingAddress='").append(mailingAddress).append('\'');
    sb.append(", phone='").append(phone).append('\'');
    sb.append(", parentId=").append(parentId);
    sb.append(", name='").append(name).append('\'');
    sb.append(", creatorId=").append(creatorId);
    sb.append(", modifierId=").append(modifierId);
    sb.append(", creator='").append(creator).append('\'');
    sb.append(", modifier='").append(modifier).append('\'');
    sb.append(", unauthorizedDate=").append(unauthorizedDate);
    sb.append(", taxOffice='").append(taxOffice).append('\'');
    sb.append(", topBanner='").append(topBanner).append('\'');
    sb.append(", transferType=").append(transferType);
    sb.append(", contact1='").append(contact1).append('\'');
    sb.append(", modifyDate=").append(modifyDate);
    sb.append(", printServiceType=").append(printServiceType);
    sb.append(", contact2='").append(contact2).append('\'');
    sb.append(", cityId=").append(cityId);
    sb.append(", pdfUserPassword='").append(pdfUserPassword).append('\'');
    sb.append(", pdfOwnerPassword='").append(pdfOwnerPassword).append('\'');
    sb.append(", sendAccountingDocuments=").append(sendAccountingDocuments);
    sb.append(", sendWinningLetters=").append(sendWinningLetters);
    sb.append(", lockPdfOfEmail=").append(lockPdfOfEmail);
    sb.append(", sendInterfaceAccountingDocuments=").append(sendInterfaceAccountingDocuments);
    sb.append(", enableDownloadReply=").append(enableDownloadReply);
    sb.append(", enableSendErrorMail=").append(enableSendErrorMail);
    sb.append(", sendAllowanceAccountingDocuments=").append(sendAllowanceAccountingDocuments);
    sb.append(", sendInterfaceAllowanceAccountingDocuments=")
        .append(sendInterfaceAllowanceAccountingDocuments);
    sb.append('}');
    return sb.toString();
  }
}
