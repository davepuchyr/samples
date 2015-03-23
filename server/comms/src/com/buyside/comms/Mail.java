package com.buyside.comms;

import java.io.File;

/**
 * @author dave
 */
public class Mail {
   public final static String CONTENT_HTML = "text/html; charset=\"UTF-8\"";
   public final static String CONTENT_TEXT = "text/plain";

   protected String sender;
   protected String[] recipients;
   protected String[] ccs;
   protected String[] bccs;
   protected String subject;
   protected String contentType = CONTENT_TEXT;

   protected Object content;

   protected File[] attachments;


   public String getSender() {
      return sender;
   }

   public void setSender( String sender ) {
      this.sender = sender;
   }

   public String[] getRecipients() {
      return recipients;
   }

   public void setRecipients( String[] recipients ) {
      this.recipients = recipients;
   }

   public String[] getCcs() {
      return ccs;
   }

   public void setCcs( String[] ccs ) {
      this.ccs = ccs;
   }

   public String[] getBccs() {
      return bccs;
   }

   public void setBccs( String[] bccs ) {
      this.bccs = bccs;
   }

   public String getSubject() {
      return subject;
   }

   public void setSubject( String subject ) {
      this.subject = subject;
   }

   public String getContentType() {
      return contentType;
   }

   public void setContentType( String contentType ) {
      this.contentType = contentType;
   }

   public void setContentTypeHTML() {
      this.contentType = CONTENT_HTML;
   }

   public void setContentTypePlainText() {
      this.contentType = CONTENT_TEXT;
   }

   public Object getContent() {
      return content;
   }

   public void setContent( Object content ) {
      this.content = content;
   }

   public File[] getAttachments() {
      return attachments;
   }

   public void setAttachments( File[] attachments ) {
      this.attachments = attachments;
   }
}
