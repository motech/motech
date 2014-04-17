package org.motechproject.hub.exception;

//@JsonSerialize(include = Inclusion.NON_NULL)
public class HubErrorDetail {

	private String reason;
    private String stackTrace;
    
 public String getReason() {
  return reason;
 }
 public void setReason(String reason) {
  this.reason = reason;
 }
 public String getStackTrace() {
  return stackTrace;
 }
 public void setStackTrace(String stackTrace) {
  this.stackTrace = stackTrace;
 }

    

}
