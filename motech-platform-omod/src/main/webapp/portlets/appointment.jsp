<%--
The contents of this file are subject to the OpenMRS Public License
Version 1.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://license.openmrs.org

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

Copyright (C) OpenMRS, LLC.  All Rights Reserved.
--%>
<%@ include file="/WEB-INF/template/include.jsp"%>


<b class="boxHeader"><spring:message code="motech.appointment.preferences"/></b>
<form method="get" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th><spring:message code="motech.appointment.preferences.service"/></th>
			<td><input name="enableReminderService" type="checkbox" checked></th>
		</tr>
		<tr>
			<th><spring:message code="motech.appointment.preferences.reminderCall"/></th>
			<td><input name="daysBefore" type="text" value="3" size="3"> <spring:message code="motech.appointment.preferences.before"/></td>
		</tr>
		<tr>
			<th><spring:message code="motech.appointment.preferences.bestTime"/></th>
			<td>
				<select name="preferredTime">
					<option value="">-- Select a Time --</option>
					<option value="0">00</option>
					<option value="1">01</option>
					<option value="2">02</option>
					<option value="3">03</option>
					<option value="4">04</option>
					<option value="5">05</option>
					<option value="6">06</option>
					<option value="7">07</option>
					<option value="8">08</option>
					<option value="9">09</option>
					<option value="10">12</option>
					<option value="11">11</option>
					<option value="12">12</option>
					<option value="13">13</option>
					<option value="14">14</option>
					<option value="15">15</option>
					<option value="16">16</option>
					<option value="17">17</option>
					<option value="18">18</option>
					<option value="19">19</option>
					<option value="20">20</option>
					<option value="21">21</option>
					<option value="22">22</option>
					<option value="23">23</option>
					<option value="24">24</option>
					<option value=""></option>
					
				</select>
				<input type="text" value="3" size="3"> <spring:message code="motech.appointment.preferences.before"/></td>
		</tr>
	</table>
	<input type="hidden" name="patientId" value="${model.patient.id}"/>
	<input type="submit" value="submit"/>
</form>
<br/>
<b class="boxHeader"><spring:message code="motech.appointment.makeAppointment"/></b>
<form method="get" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th><spring:message code="motech.appointment.windowStartDate"/></th>
			<td><openmrs:fieldGen type="java.util.Date" formFieldName="windowStartDate" val=""/></th>
		</tr>
		<tr>
			<th><spring:message code="motech.appointment.windowEndDate"/></th>
			<td><openmrs:fieldGen type="java.util.Date" formFieldName="windowEndDate" val=""/></td>
		</tr>
	</table>
	<input type="hidden" name="patientId" value="${model.patient.id}"/>
	<input type="submit" value="submit"/>
</form>
<br/>
<b class="boxHeader"><spring:message code="motech.appointment.list"/></b>
<div id="appointmentList" class="box">
	<c:choose>
		<c:when test="${not empty model.appointmentList}">
			<table cellpadding="2" cellspacing="0">
				<tr>
					<th><spring:message code="motech.appointment.windowStartDate"/></th>
					<th><spring:message code="motech.appointment.windowEndDate"/></th>
					<th><spring:message code="motech.appointment.patientArrived"/></th>
				</tr>
				<c:forEach var="appointment" items="${model.appointmentList}" varStatus="rowStatus">
					<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
						<td><openmrs:formatDate date="${appointment.windowStartDate}" type="small" /></td>
						<td><openmrs:formatDate date="${appointment.windowEndDate}" type="small" /></td>
						<td>${appointment.patientArrived}</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="motech.appointment.notfound"/>
		</c:otherwise>
	</c:choose>
</div>