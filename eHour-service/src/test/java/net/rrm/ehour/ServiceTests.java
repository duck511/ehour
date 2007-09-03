/**
 * Created on Nov 4, 2006
 * Created by Thies Edeling
 * Copyright (C) 2005, 2006 te-con, All Rights Reserved.
 *
  * This Software is copyright TE-CON 2007. This Software is not open source by definition. The source of the Software is available for educational purposes.
 * TE-CON holds all the ownership rights on the Software.
 * TE-CON freely grants the right to use the Software. Any reproduction or modification of this Software, whether for commercial use or open source,
 * is subject to obtaining the prior express authorization of TE-CON. * thies@te-con.nl
 * TE-CON
 * Legmeerstraat 4-2h, 1058ND, AMSTERDAM, The Netherlands
 *
 */

package net.rrm.ehour;


import net.rrm.ehour.customer.service.CustomerServiceTest;
import net.rrm.ehour.project.service.ProjectAssignmentServiceTest;
import net.rrm.ehour.project.service.ProjectServiceTest;
import net.rrm.ehour.report.criteria.ReportCriteriaTest;
import net.rrm.ehour.report.service.ReportCriteriaServiceTest;
import net.rrm.ehour.report.service.ReportServiceTest;
import net.rrm.ehour.report.util.ReportUtilTest;
import net.rrm.ehour.timesheet.dto.BookedDayComparatorTest;
import net.rrm.ehour.timesheet.service.TimesheetServiceTest;
import net.rrm.ehour.user.service.UserServiceTest;
import net.rrm.ehour.util.DateUtilTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({UserServiceTest.class,
				TimesheetServiceTest.class, 
				ReportServiceTest.class, 
				ReportCriteriaServiceTest.class,
				ProjectServiceTest.class,
				ProjectAssignmentServiceTest.class,
				CustomerServiceTest.class,
//				CalendarUtilTest.class,
				DateUtilTest.class,
//				AuthUtilTest.class,
//				CalendarUtilTest.class,		
				BookedDayComparatorTest.class,
//				DomainAssemblerTest.class,
//				NavCalendarTagTest.class,
//				CustomerReportTest.class,
				ReportCriteriaTest.class,
//				UserCriteriaAssemblerTest.class,
				ReportUtilTest.class
//				UserReportTest.class,
//				CustomerReportTest.class,
//				TimesheetRowComparatorTest.class,
//				TimesheetFormAssemblerTest.class,
//				ProjectReportTest.class
			})
public class ServiceTests
{
}
