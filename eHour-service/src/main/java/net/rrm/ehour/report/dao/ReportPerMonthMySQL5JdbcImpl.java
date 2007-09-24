/**
 * Created on Feb 4, 2007
 * Created by Thies Edeling
 * Copyright (C) 2005, 2006 te-con, All Rights Reserved.
 *
 * This Software is copyright TE-CON 2007. This Software is not open source by definition. The source of the Software is available for educational purposes.
 * TE-CON holds all the ownership rights on the Software.
 * TE-CON freely grants the right to use the Software. Any reproduction or modification of this Software, whether for commercial use or open source,
 * is subject to obtaining the prior express authorization of TE-CON.
 * thies@te-con.nl
 * TE-CON
 * Legmeerstraat 4-2h, 1058ND, AMSTERDAM, The Netherlands
 *
 */

package net.rrm.ehour.report.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rrm.ehour.data.DateRange;
import net.rrm.ehour.report.reports.FlatProjectAssignmentAggregate;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * TODO  
 **/

public class ReportPerMonthMySQL5JdbcImpl extends SimpleJdbcDaoSupport implements ReportPerMonthDAO
{
	/* (non-Javadoc)
	 * @see net.rrm.ehour.report.dao.ReportPerMonthDAO#getHoursPerMonthPerAssignmentForUsers(java.lang.Integer[], java.lang.Integer[], net.rrm.ehour.data.DateRange)
	 */
	@SuppressWarnings("unchecked")
	public List<FlatProjectAssignmentAggregate> getHoursPerMonthPerAssignmentForUsers(List<Serializable> userIds, List<Serializable> projectIds, DateRange dateRange)
	{
		ParameterizedRowMapper<FlatProjectAssignmentAggregate> 	mapper;
		NamedParameterJdbcTemplate 	namedParamTemp; 
		Map	params;
		
		String	sql = "SELECT SUM(ENTRY.HOURS) AS BOOKED_HOURS, " +
				"       SUM(ENTRY.HOURS * PAG.HOURLY_RATE) AS TURNOVER, " +
				"       DATE_FORMAT(ENTRY_DATE, '%U %Y') AS ENTRY_DATE, " +
				"       CUST.CUSTOMER_ID, " +
				"       CUST.NAME AS CUSTOMER_NAME, " +
				"       CUST.CODE AS CUSTOMER_CODE, " +				
				"       PAG.PROJECT_ID, " +
				"       PAG.USER_ID, " +
				"		USR.FIRST_NAME, " + 
				"		USR.LAST_NAME, " +
				"       PAG.ROLE AS ASSIGNMENT_DESC, " +
				"       PRJ.PROJECT_ID AS PROJECT_ID, " +
				"       PRJ.NAME AS PROJECT_NAME, " +
				"       PAG.ASSIGNMENT_ID AS ASSIGNMENT_ID " +
				"FROM TIMESHEET_ENTRY ENTRY, " +
				"     CUSTOMER CUST, " +
				"     PROJECT PRJ, " +
				"     PROJECT_ASSIGNMENT PAG, " +
				"     USERS USR " +
				"WHERE ENTRY.ASSIGNMENT_ID = PAG.ASSIGNMENT_ID AND " +
				"      PAG.PROJECT_ID = PRJ.PROJECT_ID AND " +
				"      PRJ.CUSTOMER_ID = CUST.CUSTOMER_ID AND " +
				"      PAG.USER_ID = USR.USER_ID AND " +
				"      PAG.USER_ID IN (:userIdList) AND " +
				"      PAG.PROJECT_ID IN (:projectIdList) AND " +
				"      (ENTRY.ENTRY_DATE >= :dateStart AND ENTRY.ENTRY_DATE <= :dateEnd) " + 
				"GROUP BY PAG.ASSIGNMENT_ID, " +
				"         DATE_FORMAT(ENTRY_DATE, '%U %Y') ";
		
		mapper = new  WeeklyProjectAssignmentAggregateRowMapper();
		namedParamTemp = new NamedParameterJdbcTemplate(getJdbcTemplate());
		
		params = new HashMap();
		params.put("userIdList", userIds);
		params.put("projectIdList", projectIds);
		params.put("dateStart", dateRange.getDateStart());
		params.put("dateEnd", dateRange.getDateEnd());

		return namedParamTemp.query(sql, params, mapper);
	}

	/* (non-Javadoc)
	 * @see net.rrm.ehour.report.dao.ReportPerMonthDAO#getHoursPerMonthPerAssignmentForUsers(java.lang.Integer[], net.rrm.ehour.data.DateRange)
	 */
	@SuppressWarnings("unchecked")
	public List<FlatProjectAssignmentAggregate> getHoursPerMonthPerAssignmentForUsers(List<Serializable> userIds, DateRange dateRange)
	{
		ParameterizedRowMapper<FlatProjectAssignmentAggregate> 	mapper;
		NamedParameterJdbcTemplate 	namedParamTemp; 
		Map	params;
		
		String	sql = "SELECT SUM(ENTRY.HOURS) AS BOOKED_HOURS, " +
				"       SUM(ENTRY.HOURS * PAG.HOURLY_RATE) AS TURNOVER, " +
				"       DATE_FORMAT(ENTRY_DATE, '%U %Y') AS ENTRY_DATE, " +
				"       CUST.CUSTOMER_ID, " +
				"       CUST.NAME AS CUSTOMER_NAME, " +
				"       CUST.CODE AS CUSTOMER_CODE, " +				
				"       PAG.PROJECT_ID, " +
				"       PAG.USER_ID, " +
				"		USR.FIRST_NAME, " + 
				"		USR.LAST_NAME, " +
				"       PAG.ROLE AS ASSIGNMENT_DESC, " +
				"       PRJ.PROJECT_ID AS PROJECT_ID, " +
				"       PRJ.NAME AS PROJECT_NAME, " +
				"       PAG.ASSIGNMENT_ID AS ASSIGNMENT_ID " +
				"FROM TIMESHEET_ENTRY ENTRY, " +
				"     CUSTOMER CUST, " +
				"     PROJECT PRJ, " +
				"     PROJECT_ASSIGNMENT PAG, " +
				"     USERS USR " +
				"WHERE ENTRY.ASSIGNMENT_ID = PAG.ASSIGNMENT_ID AND " +
				"      PAG.PROJECT_ID = PRJ.PROJECT_ID AND " +
				"      PRJ.CUSTOMER_ID = CUST.CUSTOMER_ID AND " +
				"      PAG.USER_ID = USR.USER_ID AND " +
				"      PAG.USER_ID IN (:userIdList) AND " +
				"      (ENTRY.ENTRY_DATE >= :dateStart AND ENTRY.ENTRY_DATE <= :dateEnd) " + 
				"GROUP BY PAG.ASSIGNMENT_ID, " +
				"         DATE_FORMAT(ENTRY_DATE, '%U %Y') ";
		
		mapper = new  WeeklyProjectAssignmentAggregateRowMapper();
		namedParamTemp = new NamedParameterJdbcTemplate(getJdbcTemplate());
		
		params = new HashMap();
		params.put("userIdList", userIds);
		params.put("dateStart", dateRange.getDateStart());
		params.put("dateEnd", dateRange.getDateEnd());

		return namedParamTemp.query(sql, params, mapper);
	}
	
	/* (non-Javadoc)
	 * @see net.rrm.ehour.report.dao.ReportPerMonthDAO#getHoursPerMonthPerAssignmentForProjects(java.lang.Integer[], net.rrm.ehour.data.DateRange)
	 */
	@SuppressWarnings("unchecked")
	public List<FlatProjectAssignmentAggregate> getHoursPerMonthPerAssignmentForProjects(List<Serializable> projectIds, DateRange dateRange)
	{
		ParameterizedRowMapper<FlatProjectAssignmentAggregate> 	mapper;
		NamedParameterJdbcTemplate 	namedParamTemp; 
		Map	params;
		
		String	sql = "SELECT SUM(ENTRY.HOURS) AS BOOKED_HOURS, " +
				"       SUM(ENTRY.HOURS * PAG.HOURLY_RATE) AS TURNOVER, " +
				"       DATE_FORMAT(ENTRY_DATE, '%U %Y') AS ENTRY_DATE, " +
				"       CUST.CUSTOMER_ID, " +
				"       CUST.NAME AS CUSTOMER_NAME, " +
				"       CUST.CODE AS CUSTOMER_CODE, " +				
				"       PAG.PROJECT_ID, " +
				"       PAG.USER_ID, " +
				"		USR.FIRST_NAME, " + 
				"		USR.LAST_NAME, " +
				"       PAG.ROLE AS ASSIGNMENT_DESC, " +
				"       PRJ.PROJECT_ID AS PROJECT_ID, " +
				"       PRJ.NAME AS PROJECT_NAME, " +
				"       PAG.ASSIGNMENT_ID AS ASSIGNMENT_ID " +
				"FROM TIMESHEET_ENTRY ENTRY, " +
				"     CUSTOMER CUST, " +
				"     PROJECT PRJ, " +
				"     PROJECT_ASSIGNMENT PAG, " +
				"     USERS USR " +
				"WHERE ENTRY.ASSIGNMENT_ID = PAG.ASSIGNMENT_ID AND " +
				"      PAG.PROJECT_ID = PRJ.PROJECT_ID AND " +
				"      PAG.PROJECT_ID IN (:projectIdList) AND " +
				"      PRJ.CUSTOMER_ID = CUST.CUSTOMER_ID AND " +
				"      PAG.USER_ID = USR.USER_ID AND " +
				"      (ENTRY.ENTRY_DATE >= :dateStart AND ENTRY.ENTRY_DATE <= :dateEnd) " + 
				"GROUP BY PAG.ASSIGNMENT_ID, " +
				"         DATE_FORMAT(ENTRY_DATE, '%U %Y') ";
		
		mapper = new  WeeklyProjectAssignmentAggregateRowMapper();
		namedParamTemp = new NamedParameterJdbcTemplate(getJdbcTemplate());
		
		params = new HashMap();
		params.put("projectIdList", projectIds);
		params.put("dateStart", dateRange.getDateStart());
		params.put("dateEnd", dateRange.getDateEnd());

		return namedParamTemp.query(sql, params, mapper);
	}


	/* (non-Javadoc)
	 * @see net.rrm.ehour.report.dao.ReportPerMonthDAO#getHoursPerMonthPerAssignment(net.rrm.ehour.data.DateRange)
	 */
	@SuppressWarnings("unchecked")
	public List<FlatProjectAssignmentAggregate> getHoursPerMonthPerAssignment(DateRange dateRange)
	{
		ParameterizedRowMapper<FlatProjectAssignmentAggregate> 	mapper;
		NamedParameterJdbcTemplate 	namedParamTemp; 
		Map	params;
		
		String	sql = "SELECT SUM(ENTRY.HOURS) AS BOOKED_HOURS, " +
				"       SUM(ENTRY.HOURS * PAG.HOURLY_RATE) AS TURNOVER, " +
				"       DATE_FORMAT(ENTRY_DATE, '%U %Y') AS ENTRY_DATE, " +
				"       CUST.CUSTOMER_ID, " +
				"       CUST.NAME AS CUSTOMER_NAME, " +
				"       CUST.CODE AS CUSTOMER_CODE, " +				
				"       PAG.PROJECT_ID, " +
				"       PAG.USER_ID, " +
				"		USR.FIRST_NAME, " + 
				"		USR.LAST_NAME, " +
				"       PAG.ROLE AS ASSIGNMENT_DESC, " +
				"       PRJ.PROJECT_ID AS PROJECT_ID, " +
				"       PRJ.NAME AS PROJECT_NAME, " +
				"       PAG.ASSIGNMENT_ID AS ASSIGNMENT_ID " +
				"FROM TIMESHEET_ENTRY ENTRY, " +
				"     CUSTOMER CUST, " +
				"     PROJECT PRJ, " +
				"     PROJECT_ASSIGNMENT PAG, " +
				"     USERS USR " +
				"WHERE ENTRY.ASSIGNMENT_ID = PAG.ASSIGNMENT_ID AND " +
				"      PAG.PROJECT_ID = PRJ.PROJECT_ID AND " +
				"      PRJ.CUSTOMER_ID = CUST.CUSTOMER_ID AND " +
				"      PAG.USER_ID = USR.USER_ID AND " +
				"      (ENTRY.ENTRY_DATE >= :dateStart AND ENTRY.ENTRY_DATE <= :dateEnd) " + 
				"GROUP BY PAG.ASSIGNMENT_ID, " +
				"         DATE_FORMAT(ENTRY_DATE, '%U %Y') ";
		
		mapper = new  WeeklyProjectAssignmentAggregateRowMapper();
		namedParamTemp = new NamedParameterJdbcTemplate(getJdbcTemplate());
		
		params = new HashMap();
		params.put("dateStart", dateRange.getDateStart());
		params.put("dateEnd", dateRange.getDateEnd());

		return namedParamTemp.query(sql, params, mapper);	
	}	


	/**
	 * Get hours per day for assignments
	 * @param assignmentId
	 * @param dateRange
	 * @return
	 */	
	@SuppressWarnings("unchecked")
	public List<FlatProjectAssignmentAggregate> getHoursPerDayForAssignment(List<Integer> assignmentIds, DateRange dateRange)
	{
		ParameterizedRowMapper<FlatProjectAssignmentAggregate> 	mapper;
		NamedParameterJdbcTemplate 	namedParamTemp; 
		Map	params;
		
		String	sql = "SELECT SUM(ENTRY.HOURS) AS BOOKED_HOURS, " +
				"       SUM(ENTRY.HOURS * PAG.HOURLY_RATE) AS TURNOVER, " +
				"       DATE_FORMAT(ENTRY_DATE, '%d%m%Y') AS ENTRY_DATE, " +
				"       CUST.CUSTOMER_ID, " +
				"       CUST.NAME AS CUSTOMER_NAME, " +
				"       CUST.CODE AS CUSTOMER_CODE, " +				
				"       PAG.PROJECT_ID, " +
				"       PAG.USER_ID, " +
				"		USR.FIRST_NAME, " + 
				"		USR.LAST_NAME, " +
				"       PAG.ROLE AS ASSIGNMENT_DESC, " +
				"       PRJ.PROJECT_ID AS PROJECT_ID, " +
				"       PRJ.NAME AS PROJECT_NAME, " +
				"       PAG.ASSIGNMENT_ID AS ASSIGNMENT_ID " +
				"FROM TIMESHEET_ENTRY ENTRY, " +
				"     CUSTOMER CUST, " +
				"     PROJECT PRJ, " +
				"     PROJECT_ASSIGNMENT PAG, " +
				"     USERS USR " +
				"WHERE ENTRY.ASSIGNMENT_ID = PAG.ASSIGNMENT_ID AND " +
				"      PAG.PROJECT_ID = PRJ.PROJECT_ID AND " +
				"      PRJ.CUSTOMER_ID = CUST.CUSTOMER_ID AND " +
				"      PAG.USER_ID = USR.USER_ID AND " +
				"      PAG.ASSIGNMENT_ID IN (:assignmentId) AND " +				
				"      (ENTRY.ENTRY_DATE >= :dateStart AND ENTRY.ENTRY_DATE <= :dateEnd) " + 
				"GROUP BY PAG.ASSIGNMENT_ID, " +
				"         DATE_FORMAT(ENTRY_DATE, '%d%m%Y') ";
		
		mapper = new  WeeklyProjectAssignmentAggregateRowMapper();
		namedParamTemp = new NamedParameterJdbcTemplate(getJdbcTemplate());
		
		params = new HashMap();
		params.put("dateStart", dateRange.getDateStart());
		params.put("dateEnd", dateRange.getDateEnd());
		params.put("assignmentId", assignmentIds);

		return namedParamTemp.query(sql, params, mapper);
	}	
	
	/**
	 * Row mapper for FlatProjectAssignmentAggregate
	 * @author Thies
	 *
	 */
	private class WeeklyProjectAssignmentAggregateRowMapper implements ParameterizedRowMapper<FlatProjectAssignmentAggregate>
	{
        public FlatProjectAssignmentAggregate mapRow(ResultSet rs, int rowNum) throws SQLException
        {
        	FlatProjectAssignmentAggregate aggregate = new FlatProjectAssignmentAggregate();
        	
        	aggregate.setAssignmentDesc(rs.getString("ASSIGNMENT_DESC"));
        	aggregate.setAssignmentId(rs.getInt("ASSIGNMENT_ID"));
        	aggregate.setCustomerId(rs.getInt("CUSTOMER_ID"));
        	aggregate.setCustomerName(rs.getString("CUSTOMER_NAME"));
        	aggregate.setCustomerCode(rs.getString("CUSTOMER_CODE"));
        	aggregate.setProjectName(rs.getString("PROJECT_NAME"));
        	aggregate.setProjectId(rs.getInt("PROJECT_ID"));
        	aggregate.setProjectName(rs.getString("PROJECT_NAME"));
        	aggregate.setTotalHours(rs.getFloat("BOOKED_HOURS"));
        	aggregate.setTotalTurnOver(rs.getFloat("TURNOVER"));
        	aggregate.setUserFirstName(rs.getString("FIRST_NAME"));
        	aggregate.setUserLastName(rs.getString("LAST_NAME"));
        	aggregate.setEntryDate(rs.getString("ENTRY_DATE"));

        	return aggregate;
        }		
	}
}
