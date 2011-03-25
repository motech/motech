<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) 2010-11 The Trustees of Columbia University in the City of
    New York and Grameen Foundation USA.  All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

    3. Neither the name of Grameen Foundation USA, Columbia University, or
    their respective contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
    AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
    FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
    USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
    OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
    EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--%>
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