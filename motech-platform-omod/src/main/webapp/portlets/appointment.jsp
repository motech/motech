<%@ include file="/WEB-INF/template/include.jsp"%>


<b class="boxHeader"><spring:message code="motech.appointment.preferences"/></b>
<form method="get" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th><spring:message code="motech.appointment.preferences.service"/></th>
			<td><input name="" type="checkbox" checked></th>
		</tr>
		<tr>
			<th><spring:message code="motech.appointment.preferences.reminderCall"/></th>
			<td><input type="text" value="3" size="3"> <spring:message code="motech.appointment.preferences.before"/></td>
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