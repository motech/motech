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

<openmrs:require privilege="Visit Scheduler - View Patient Dashboard" otherwise="/login.htm" redirect="/module/visitscheduler/scheduleForm.jsp" />

<b class="boxHeader"><spring:message code="visitscheduler.patientDashboard.forms.search"/></b>
<form method="get" class="box">
	<spring:message code="visitscheduler.appointment.date"/>
	<openmrs:fieldGen type="java.util.Date" formFieldName="date" val="${model.date}"/>
	<input type="hidden" name="patientId" value="${model.patient.id}"/>
	<input type="submit" value="<spring:message code="visitscheduler.schedule.view"/>"/>
</form>
<br/>
<b class="boxHeader"><spring:message code="visitscheduler.patientDashboard.forms"/></b>
<div id="appointmentList" class="box">
	<c:choose>
		<c:when test="${model.numAppointments > 0}">
			<table cellpadding="2" cellspacing="0" style="width: 100%">
				<tr>
					<th><spring:message code="visitscheduler.appointment.date"/></th>
					<th><spring:message code="visitscheduler.appointment.time"/></th>
					<th><spring:message code="visitscheduler.appointment.activity"/></th>
					<th><spring:message code="visitscheduler.appointment.provider"/></th>
				</tr>
				<c:forEach var="appointment" items="${model.appointmentList}" varStatus="rowStatus">
					<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
						<td>${appointment.schedule.formattedStartDate}</td>
						<td>${appointment.schedule.formattedStartTime}</td>
						<td>${appointment.schedule.activity.code}</td>
						<td>${appointment.schedule.user.personName}</td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<spring:message code="visitscheduler.appointment.notfound"/>
		</c:otherwise>
	</c:choose>
</div>